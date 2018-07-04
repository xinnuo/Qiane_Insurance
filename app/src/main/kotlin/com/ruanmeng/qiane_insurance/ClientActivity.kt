package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.header_client.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class ClientActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private val listBirth = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        init_title("客户", "搜索客户")

        EventBus.getDefault().register(this@ClientActivity)

        swipe_refresh.isRefreshing = true
        getData()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        recycle_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String? = if (pos == 0) null else list[pos - 1].fristName.toUpperCase()
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(baseContext).inflate(R.layout.header_client, null)
                    addHeader(view)
                }
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_client_name, data.customerName)
                            .visibility(R.id.item_client_divider1,
                                    if ((!isLast && data.fristName != list[list.indexOf(data) + 1].fristName) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_client) {
                                startActivity<ClientAddActivity>(
                                        "title" to "客户信息",
                                        "usercustomerId" to data.usercustomerId)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<ClientSearchActivity>()
            R.id.client_birth_ll -> startActivity<ClientBirthActivity>("list" to listBirth)
            R.id.client_form_ll -> startActivity<ClientFormActivity>()
            R.id.client_edit -> startActivity<ClientEditActivity>()
            R.id.client_input -> startActivity<ClientAddActivity>("title" to "添加客户")
            R.id.client_contact -> startActivityForResult(Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI),
                    10)
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return

        var userName = ""
        var userBirth = ""
        val userPhones = ArrayList<String>()
        val userEmails = ArrayList<String>()

        if (resultCode == RESULT_OK && requestCode == 10) {
            val reContentResolver = contentResolver
            val cursor = managedQuery(data.data, null, null, null, null)
            cursor.moveToFirst()
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

            //查询电话类型的数据操作
            val phone = reContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null)
            while (phone.moveToNext()) {
                val number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                userPhones.add(number.replace("-", "").replace(" ", ""))
            }
            phone.close()

            //查询Email类型的数据操作
            val emails = reContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                    null,
                    null)
            while (emails.moveToNext()) {
                val email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))

                userEmails.add(email)
            }
            emails.close()

            //查询生日的数据操作
            val event = reContentResolver.query(ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + " = " + contactId,
                    null,
                    ContactsContract.Contacts.Data.RAW_CONTACT_ID)
            while (event.moveToNext()) {
                // 取得mimetype类型，扩展的数据都在这个类型里面
                val mimetype = event.getString(event.getColumnIndex(ContactsContract.Data.MIMETYPE))
                if (mimetype == ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE) {

                    val eventType = event.getInt(event.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE))
                    if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
                        userBirth = event.getString(event.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE))
                    }
                }
            }
            event.close()
        }

        startActivity<ClientAddActivity>(
                "title" to "添加客户",
                "name" to userName,
                "birth" to userBirth,
                "phone" to userPhones.joinToString(","),
                "email" to userEmails.joinToString(","))
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.find_usercustome_info)
                .tag(this@ClientActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val data = response.body().`object` ?: return
                        client_num.text = data.customerCtn
                        client_birth.text = "最近有${data.birthCtn}位客户过生日"
                        client_form.text = "最近有${data.ordeDayCtn}个保单到期"
                        client_all.text = "(${data.customerCtn}人)"

                        list.clear()
                        listBirth.clear()
                        list.addItems(data.customerList)
                        listBirth.addItems(data.birthList)

                        mAdapterEx.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ClientActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "更新客户", "删除客户" -> {
                swipe_refresh.isRefreshing = true
                getData()
            }
        }
    }
}
