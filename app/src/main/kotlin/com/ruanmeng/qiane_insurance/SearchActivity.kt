package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.Tools
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.sdk25.listeners.textChangedListener
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import java.util.ArrayList

class SearchActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var listHistory = ArrayList<String>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setToolbarVisibility(false)
        init_title()

        updateHistory()
    }

    override fun init_title() {
        search_edit.hint = "请输入产品，计划书"
        empty_hint.text = "未搜索到相关信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_first_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_first_name, getColorText(data.title, keyWord))
                            .text(R.id.item_first_price, "${data.productSum}元")
                            .text(R.id.item_first_content, data.synopsis)
                            .text(R.id.item_first_tui,
                                    "推广费${if (data.recommendSum.isEmpty()) 0 else (data.recommendSum.toDouble() * 100).toInt()}%")

                            .with<ImageView>(R.id.item_first_img) {
                                it.setImageURL(BaseHttp.baseImg + data.img)
                            }

                            .visibility(R.id.item_first_price, if (data.type == "1") View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_tui, if (data.type == "1") View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) {
                                if (data.type == "1")
                                    startActivity<PlanMakeActivity>(
                                            "title" to data.title,
                                            "prospectusId" to data.productprospectusId)
                                else startActivity<PlanLookActivity>(
                                        "productinId" to data.productprospectusId,
                                        "type" to "产品详情")
                            }
                }
                .attachTo(recycle_list)

        search_hot.setOnTagClickListener { _, position, _ ->
            keyWord = listHistory[position]
            search_edit.setText(keyWord)
            updateList()
            return@setOnTagClickListener true
        }

        search_edit.textChangedListener {
            onTextChanged { s, _, _, _ ->
                if (s!!.isEmpty()) {
                    updateHistory()
                    search_history.visible()
                    empty_view.gone()

                    if (keyWord.isNotEmpty()) keyWord = ""
                }
            }
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.toString().isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = search_edit.text.toString()

                    val arr = if (getString("history").isEmpty()) JSONArray() else JSONArray(getString("history"))
                    if (!arr.toString().contains(keyWord)) arr.put(keyWord)
                    putString("history", arr.toString())

                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.index_productprospectus)
                .tag(this@SearchActivity)
                .params("key", keyWord)
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

    private fun updateHistory() {
        listHistory = Tools.jsonArrayToList(getString("history"))
        search_hot.adapter = object : TagAdapter<String>(listHistory) {

            override fun getView(parent: FlowLayout, position: Int, item: String): View {
                val flowTitle = View.inflate(baseContext, R.layout.item_search_hot_flow, null) as TextView
                flowTitle.text = item
                return flowTitle
            }

        }
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true
        search_history.gone()
        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> {
                if (search_edit.text.isNotEmpty()) search_edit.setText("")
                else ActivityStack.screenManager.popActivities(this::class.java)
            }
            R.id.search_del -> {
                putString("history", "")
                updateHistory()
            }
        }
    }
}
