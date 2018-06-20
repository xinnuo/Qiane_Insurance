package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class PlanActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)
        init_title("计划书")

        getData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_plan_list) { data, injector ->

                    val position = list.indexOf(data)
                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_plan_name, data.title)
                            .text(R.id.item_plan_content, data.content)

                            .with<ImageView>(R.id.item_plan_img) {}

                            .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider3, if (position != 0) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_plan) {
                                startActivity<PlanMakeActivity>()
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_company_ll -> startActivity<CompanyActivity>("type" to "计划书")
        }
    }

    override fun getData() {
        list.clear()

        list.add(CommonData().apply {
            title = "平安e生保2108版"
            content = "1万住院医疗+1万意外医疗+10万身价保障"
        })
        list.add(CommonData().apply {
            title = "平安意外险2018"
            content = "旗舰产品，强势升级"
        })
        list.add(CommonData().apply {
            title = "少儿平安福2018"
            content = "凸显保障，期满领取保费"
        })
        list.add(CommonData().apply {
            title = "平安意外险"
            content = "旗舰产品，强势升级"
        })

        mAdapter.updateData(list)

        swipe_refresh.isRefreshing = false
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
}
