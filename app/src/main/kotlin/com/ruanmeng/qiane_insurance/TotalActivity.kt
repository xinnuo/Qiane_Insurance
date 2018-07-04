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
import org.jetbrains.anko.*
import java.util.ArrayList

class TotalActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("累计获客")

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        mAdapter.updateData(list)

        TODO("无用的页面")
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关客户信息"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_form_list) { data, injector ->
                    injector.visibility(R.id.item_form_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_form) {
                                startActivity<OrderDetailActivity>()
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
