package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.qiane_insurance.PlanLookActivity
import com.ruanmeng.qiane_insurance.R
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.isNumeric
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.include
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.verticalLayout
import java.text.DecimalFormat
import java.util.*

class OrderFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = createView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        empty_hint.text = "暂无相关订单信息"
        swipe_refresh.refresh { getData(1) }
        activity?.let {
            recycle_list.load_Linear(it, swipe_refresh) {
                if (!isLoadingMore) {
                    isLoadingMore = true
                    getData(pageNum)
                }
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
                                "4" -> "已生效"
                                "5" -> "已失效"
                                else -> ""
                            })
                            .text(R.id.item_order_around,
                                    "推广费${if (data.spreadRate.isNumeric()) {
                                        DecimalFormat("0.##").format(data.spreadRate.toDouble() * 100)
                                    } else "0"}%")

                            .visibility(R.id.item_order_pay,
                                    if (data.status in "-1,0,1") View.VISIBLE else View.GONE)
                            .visibility(R.id.item_order_divider,
                                    if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .with<ImageView>(R.id.item_order_img) {
                                it.setImageURL(BaseHttp.baseImg + data.productImg)
                            }

                            .clicked(R.id.item_order_pay) {
                                startActivity<PlanLookActivity>(
                                        "type" to "订单支付",
                                        "goodsOrderId" to data.goodsOrderId)
                            }

                            .clicked(R.id.item_order) {
                                startActivity<PlanLookActivity>(
                                        "type" to "订单详情",
                                        "goodsOrderId" to data.goodsOrderId)
                            }
                }
                .attachTo(recycle_list)
    }

    private fun createView(): View {
        return UI {
            verticalLayout {
                include<View>(R.layout.layout_list)
            }
        }.view
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.goodsorder_list_data)
                .tag(this@OrderFragment)
                .headers("token", getString("token"))
                .params("status", arguments?.getString("status", "") ?: "")
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity) {

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

    override fun onResume() {
        EventBus.getDefault().unregister(this@OrderFragment)
        super.onResume()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this@OrderFragment)
        super.onStop()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "订单支付" -> {
                swipe_refresh.isRefreshing = true
                getData(1)
            }
        }
    }
}
