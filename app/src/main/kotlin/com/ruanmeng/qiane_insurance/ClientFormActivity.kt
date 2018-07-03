package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import org.jetbrains.anko.include
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout
import java.util.ArrayList

class ClientFormActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("保单到期")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关到期订单信息"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_form_list) { data, injector ->
                    injector.text(R.id.item_form_title, data.productName)
                            .text(R.id.item_form_plan, "投保人：${data.buyName}")
                            .text(R.id.item_form_planed, "被保人：${data.coverName}")
                            .text(R.id.item_form_range,
                                    "投保期间：${data.startDate.replace("-", ".")} - ${data.endDate.replace("-", ".")}")
                            .visibility(R.id.item_form_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .with<ImageView>(R.id.item_form_img) {
                                it.setImageURL(BaseHttp.baseImg + data.productImg)
                            }

                            .clicked(R.id.item_form) {
                                startActivity<WebActivity>(
                                        "title" to "订单详情",
                                        "goodsOrderId" to data.goodsOrderId)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.goodsorder_list_data)
                .tag(this@ClientFormActivity)
                .headers("token", getString("token"))
                .params("type", "1")
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
}
