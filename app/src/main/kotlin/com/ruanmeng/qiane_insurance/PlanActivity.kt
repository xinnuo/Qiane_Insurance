package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.KeyboardHelper
import kotlinx.android.synthetic.main.activity_plan.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.listeners.onEditorAction
import org.jetbrains.anko.sdk25.listeners.textChangedListener
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class PlanActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var companyId = ""
    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)
        init_title("计划书")

        EventBus.getDefault().register(this@PlanActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        companyId = getString("companyId")
        plan_company.text = getString("companyName")

        empty_hint.text = "暂无相关计划书信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_plan_list) { data, injector ->

                    val position = list.indexOf(data)
                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_plan_name, getColorText(data.prospectusTitle, keyword))
                            .text(R.id.item_plan_content, data.synopsis)

                            .with<ImageView>(R.id.item_plan_img) {
                                it.setImageURL(BaseHttp.baseImg + data.prospectusImg)
                            }

                            .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider3, if (position != 0) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_plan) {
                                startActivity<PlanMakeActivity>(
                                        "title" to data.prospectusTitle,
                                        "prospectusId" to data.prospectusId)
                            }
                }
                .attachTo(recycle_list)

        plan_edit.textChangedListener {
            onTextChanged { s, _, _, _ ->
                if (s!!.isEmpty() && keyword.isNotEmpty()) {
                    keyword = ""
                    updateList()
                }
            }
        }
        plan_edit.onEditorAction { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (plan_edit.text.toString().isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyword = plan_edit.text.toString()
                    updateList()
                }
            }
            return@onEditorAction false
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_company_ll -> startActivity<CompanyActivity>(
                    "type" to "计划书",
                    "companyId" to companyId)
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.prospectus_list_data)
                .tag(this@PlanActivity)
                .isMultipart(true)
                .params("companyId", companyId)
                .params("title", keyword)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.apply { if (list.isEmpty()) visible() else gone() }
                    }

                })
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@PlanActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "计划书" -> {
                companyId = event.id
                plan_company.text = event.name

                updateList()
            }
        }
    }
}
