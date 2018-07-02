package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_order.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.design.listeners.onTabSelectedListener
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class OrderActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var tabPosition = 0
    private var mStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        init_title("我的订单")
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
                onTabSelected {
                    mStatus = when (it!!.position) {
                        1 -> "0"
                        2 -> "3"
                        else -> ""
                    }

                    OkGo.getInstance().cancelTag(this@OrderActivity)
                    window.decorView.postDelayed({ updateList() }, 300)
                }
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
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.goodsorder_list_data)
                .tag(this@OrderActivity)
                .headers("token", getString("token"))
                .params("status", mStatus)
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

        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }
}
