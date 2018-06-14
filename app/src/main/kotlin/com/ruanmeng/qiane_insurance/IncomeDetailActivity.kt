package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.include
import org.jetbrains.anko.verticalLayout
import java.util.ArrayList

class IncomeDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("收入明细")

        list.add(CommonData().apply {
            title = "2018-05-18 16:56"
            content = "账户提现"
            price = "160"
        })
        list.add(CommonData().apply {
            title = "2018-03-28 13:23"
            content = "账户提现"
            price = "230"
        })
        list.add(CommonData().apply {
            title = "2018-02-02 09:45"
            content = "账户提现"
            price = "100"
        })
        list.add(CommonData().apply {
            title = "2018-01-06 20:26"
            content = "账户提现"
            price = "400"
        })

        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关收入明细！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_income_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_income_time, data.title)
                            .text(R.id.item_income_content, data.content)
                            .text(R.id.item_income_money, "+${data.price}")

                            .visibility(R.id.item_income_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_income_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
