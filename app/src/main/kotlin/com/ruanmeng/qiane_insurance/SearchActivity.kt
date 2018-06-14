package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_title_search.*

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        search_edit.hint = "请输入产品，计划书"

        val items = listOf("中国平安", "中国人寿", "新华保险",
                "太平洋保险", "平安福", "国寿福", "平安福2018",
                "国寿福2018")
        search_hot.adapter = object : TagAdapter<String>(items) {

            override fun getView(parent: FlowLayout, position: Int, item: String): View {
                val flowTitle = View.inflate(baseContext, R.layout.item_search_hot_flow, null) as TextView
                flowTitle.text = item
                return flowTitle
            }

        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> {
                if (search_edit.text.isNotEmpty()) search_edit.setText("")
                else ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }
}
