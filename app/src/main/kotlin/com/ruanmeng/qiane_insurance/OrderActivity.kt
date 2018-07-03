package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_order.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.isNumeric
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.design.listeners.onTabSelectedListener
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
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
                    injector.text(R.id.item_order_time, data.createDate)
                            .text(R.id.item_order_title, data.productName)
                            .text(R.id.item_order_plan, "投保人：${data.buyName}")
                            .text(R.id.item_order_planed, "被保人：${data.coverName}")
                            .text(R.id.item_order_money, "订单金额：${data.payCost}元")
                            .text(R.id.item_order_range,
                                    "投保期间：${data.startDate.replace("-", ".")} - ${data.endDate.replace("-", ".")}")
                            .text(R.id.item_order_status, when (data.status) {
                                "-3" -> "已删除"
                                "-2" -> "投保中"
                                "-1" -> "付款失败"
                                "0" -> "待付款"
                                "1" -> "付款中"
                                "2" -> "已付款"
                                "3" -> "已出单"
                                else -> ""
                            })
                            .text(R.id.item_order_around,
                                    "推广费${if (data.spreadRate.isNumeric()) {
                                        DecimalFormat("0.##").format(data.spreadRate.toDouble() * 100)
                                    } else "0"}%")

                            .visibility(R.id.item_order_pay,
                                    if (data.status == "-1" || data.status == "0") View.VISIBLE else View.GONE)
                            .visibility(R.id.item_order_divider,
                                    if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .with<ImageView>(R.id.item_order_img) {
                                it.setImageURL(BaseHttp.baseImg + data.productImg)
                            }

                            .clicked(R.id.item_order_pay) {}

                            .clicked(R.id.item_order) {
                                startActivity<WebActivity>(
                                        "title" to "订单详情",
                                        "goodsOrderId" to data.goodsOrderId)
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
