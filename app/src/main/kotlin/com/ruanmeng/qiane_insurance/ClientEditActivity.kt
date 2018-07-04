package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.activity_client_edit.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onTouch
import kotlin.collections.ArrayList

class ClientEditActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private lateinit var totalTV: TextView
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_edit)
        init_title("编辑", "管理通讯录")

        EventBus.getDefault().register(this@ClientEditActivity)

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        client_list.load_Linear(baseContext)

        client_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String? = if (pos == 0) null else list[pos - 1].fristName.toUpperCase()
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(createView())
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val index = list.indexOf(data)
                    val isLast = index == list.size - 1

                    injector.text(R.id.item_client_name, data.customerName)
                            .checked(R.id.item_client_check, data.isChecked)
                            .visibility(R.id.item_client_check, if (isEdit) View.VISIBLE else View.GONE)
                            .visibility(R.id.item_client_divider1,
                                    if ((!isLast && data.fristName != list[index + 1].fristName) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_client) {
                                if (isEdit) {
                                    data.isChecked = !data.isChecked
                                    mAdapterEx.notifyDataSetChanged()

                                    client_all.isChecked = list.none { !it.isChecked }
                                }
                                else startActivity<ClientAddActivity>(
                                        "title" to "客户信息",
                                        "usercustomerId" to data.usercustomerId)
                            }
                }
                .attachTo(client_list)

        tvRight.setOneClickListener(View.OnClickListener {
            isEdit = !isEdit
            updateUI()
        })

        client_all.onTouch { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val isChecked = client_all.isChecked
                client_all.isChecked = !isChecked
                list.forEach { it.isChecked = !isChecked }
                mAdapterEx.notifyDataSetChanged()
            }
            return@onTouch true
        }
    }

    private fun createView(): View {
        return UI {
            verticalLayout {
                lparams(height = wrapContent)

                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dip(15), 0, 0, 0)
                    backgroundColorResource = R.color.white

                    themedTextView("所有客户", R.style.Font15_black)

                    totalTV = themedTextView("(0人)", R.style.Font13_black) {
                        textColorResource = R.color.colorAccent
                    }
                }.lparams(width = matchParent, height = dip(45))

                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))
            }
        }.view
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        tvRight.text = if (isEdit) "取消" else "管理通讯录"
        if (isEdit) {
            client_add.gone()
            client_edit.visible()
        } else {
            client_add.visible()
            client_edit.gone()
        }
        client_all.isChecked = false
        totalTV.text = "(${list.size}人)"

        list.forEach { it.isChecked = false }
        mAdapterEx.notifyDataSetChanged()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_input -> startActivity<ClientAddActivity>("title" to "添加客户")
            R.id.client_contact -> startActivityForResult(Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI),
                    10)
            R.id.client_del -> {
                if (list.none { it.isChecked }) {
                    showToast("请选择要删除的客户")
                    return
                }

                alert {
                    title = "温馨提示"
                    message = "确定要删除所选择的客户吗？"
                    negativeButton("取消") {}
                    positiveButton("删除") {
                        val itemIds = ArrayList<String>()
                        list.filter { it.isChecked }.forEach { itemIds.add(it.usercustomerId) }

                        OkGo.post<String>(BaseHttp.delete_usercustomer_sub)
                                .tag(this@ClientEditActivity)
                                .headers("token", getString("token"))
                                .params("ids", itemIds.joinToString(","))
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                        showToast(msg)
                                        EventBus.getDefault().post(RefreshMessageEvent("删除客户"))
                                        list.removeAll { it.isChecked }
                                        isEdit = false
                                        updateUI()
                                    }

                                })
                    }
                }.show()
            }
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
                .tag(this@ClientEditActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val data = response.body().`object` ?: return
                        totalTV.text = "(${data.customerCtn}人)"

                        list.clear()
                        list.addItems(data.customerList)

                        mAdapterEx.updateData(list)
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ClientEditActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "更新客户" -> getData()
        }
    }
}
