package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.include
import org.jetbrains.anko.verticalLayout

class ReadActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("计划书阅读")

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        list.add(CommonData("3"))
        list.add(CommonData("4"))
        list.add("更早")
        list.add(CommonData("5"))
        list.add(CommonData("6"))
        list.add(CommonData("7"))
        list.add(CommonData("8"))
        list.add(CommonData("9"))
        list.add(CommonData("10"))
        mAdapterEx.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(baseContext, R.layout.header_read)
                .register<String>(R.layout.item_read_divider) { data, injector ->
                    injector.text(R.id.item_divider_hint, data)
                }
                .register<CommonData>(R.layout.item_read_list) { data, injector ->

                    val position = list.indexOf(data)
                    val isLast = list.indexOf(data) == list.size - 1

                    injector.visibility(R.id.item_read_divider1, if (isLast || position == 3) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_read_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
