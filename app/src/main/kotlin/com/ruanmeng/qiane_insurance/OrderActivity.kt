package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v4.app.Fragment
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.OrderFragment
import java.util.*
import com.ruanmeng.fragment.TabFragmentAdapter
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.collections.forEachWithIndex

class OrderActivity : BaseActivity() {

    private var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        init_title("我的订单")
    }

    override fun init_title() {
        super.init_title()
        tabPosition = intent.getIntExtra("position", 0)

        val titles = listOf("全部", "待付款", "付款失败", "付款中", "已付款", "已出单", "已生效", "已失效")
        val fragments = ArrayList<Fragment>()
        titles.forEachWithIndex { position, _ ->
            fragments.add(OrderFragment().apply {
                arguments = Bundle().apply { putString("status", when (position) {
                    1 -> "0"
                    2 -> "-1"
                    3 -> "1"
                    4 -> "2"
                    5 -> "3"
                    6 -> "4"
                    7 -> "5"
                    else -> ""
                }) }
            })
        }

        order_tab.removeAllTabs()
        order_container.adapter = TabFragmentAdapter(supportFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        order_tab.setupWithViewPager(order_container)
        order_tab.getTabAt(tabPosition)?.select()
    }
}
