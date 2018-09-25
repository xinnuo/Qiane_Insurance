package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.activity_client_search.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.dip
import org.jetbrains.anko.sdk25.listeners.textChangedListener
import org.jetbrains.anko.sp
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class ClientSearchActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_search)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@ClientSearchActivity)

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        empty_hint.text = "未搜索到相关客户信息！"
        client_list.load_Linear(baseContext)

        client_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String = list[pos].fristName.toUpperCase()
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val index = list.indexOf(data)
                    val isLast = index == list.size - 1

                    injector.text(R.id.item_client_name, getColorText(data.customerName, keyWord))
                            .visibility(R.id.item_client_divider1,
                                    if ((!isLast && data.fristName != list[index + 1].fristName) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_client) {
                                val isPlan = intent.getBooleanExtra("isPlan", false)
                                if (isPlan) {
                                    EventBus.getDefault().post(RefreshMessageEvent(
                                            "选择客户",
                                            data.customerSex,
                                            data.customerName))

                                    ActivityStack.screenManager.popActivities(this@ClientSearchActivity::class.java)
                                } else startActivity<ClientAddActivity>(
                                        "title" to "客户信息",
                                        "usercustomerId" to data.usercustomerId)
                            }
                }
                .attachTo(client_list)

        val letters = listOf("A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z", "#")
        client_index.setIndexBarHeightRatio(0.9f)
        client_index.indexBar.apply {
            setNorTextColor(resources.getColor(R.color.gray))
            setSelTextColor(resources.getColor(R.color.black))
            setIndexsList(letters)
            setIndexChangeListener { name ->
                val itemIndex = list.indexOfFirst { it.fristName.toUpperCase() == name }
                if (itemIndex > -1)
                    (client_list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(itemIndex, 0)
            }
        }

        client_edit.textChangedListener {
            onTextChanged { s, _, _, _ ->
                if (s!!.isEmpty() && keyWord.isNotEmpty()) {
                    keyWord = ""
                    updateList()
                }
            }
        }
        client_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (client_edit.text.toString().isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = client_edit.text.trim().toString()
                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_cancel -> client_edit.setText("")
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.find_usercustome_data)
                .tag(this@ClientSearchActivity)
                .headers("token", getString("token"))
                .params("customerName", keyWord)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        empty_view.apply { if (list.isEmpty()) visible() else gone() }
                    }

                })
    }

    private fun updateList() {
        OkGo.getInstance().cancelTag(this@ClientSearchActivity)
        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        getData()
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ClientSearchActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "更新客户" -> getData()
        }
    }
}
