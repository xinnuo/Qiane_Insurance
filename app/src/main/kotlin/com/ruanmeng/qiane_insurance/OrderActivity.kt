package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_order.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.design.listeners.onTabSelectedListener
import org.jetbrains.anko.dip
import java.util.ArrayList


class OrderActivity : BaseActivity() {

    private var tabPosition = 0
    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        init_title("我的订单")

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        list.add(CommonData("3"))
        list.add(CommonData("4"))
        list.add(CommonData("5"))
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        tabPosition = intent.getIntExtra("position", 0)

        order_tab.apply {
            (getChildAt(0) as LinearLayout).apply {
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                dividerDrawable = ContextCompat.getDrawable(baseContext, R.drawable.divider_vertical)
                dividerPadding = dip(15)
            }

            onTabSelectedListener {
                onTabSelected { tab -> }
            }

            addTab(this.newTab().setText("全部"), tabPosition == 0)
            addTab(this.newTab().setText("待付款"), tabPosition == 1)
            addTab(this.newTab().setText("已出单"), tabPosition == 2)

            post { Tools.setIndicator(this, 30, 30) }
        }

        empty_hint.text = "暂无相关订单信息"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_order_list) { data, injector ->
                    injector.visibility(R.id.item_order_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_order) {
                                startActivity<OrderDetailActivity>()
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
