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
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.header_read.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.include
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout

class ReadActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("计划书阅读")

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(baseContext, R.layout.header_read)
                .register<String>(R.layout.item_read_divider) { data, injector ->
                    injector.text(R.id.item_divider_hint, data)
                }
                .register<CommonData>(R.layout.item_read_list) { data, injector ->

                    val dividerPos = list.indexOfFirst { it is String } - 1
                    val position = list.indexOf(data)
                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_read_name, data.prospectusTitle)
                            .text(R.id.item_read_age, "${if (data.coverSex == "0") "女" else "男"} ${data.coverAge}岁")
                            .text(R.id.item_read_money, "年交${if (data.insuranceSum.isEmpty()) "0" else data.insuranceSum}元")
                            .text(R.id.item_read_content, "最后查看：${data.lastReadTime}")

                            .with<ImageView>(R.id.item_read_img) {
                                it.setImageURL(BaseHttp.baseImg + data.prospectusImg)
                            }

                            .visibility(R.id.item_read_divider1, if (isLast || position == dividerPos) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_read_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_read) {
                                startActivity<PlanLookActivity>(
                                        "title" to data.prospectusTitle,
                                        "prospectusId" to data.prospectusId,
                                        "type" to "计划书")
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.read_prospectus_info)
                .tag(this@ReadActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val data = response.body().`object` ?: return
                        read_make.text = data.makeSum
                        read_num.text = data.readSum

                        list.clear()
                        val items = ArrayList<CommonData>()
                        list.addItems(data.lwithin)
                        items.addItems(data.lago)

                        if (items.isNotEmpty()) {
                            list.add("更早")
                            list.addAll(items)
                        }

                        mAdapterEx.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }
}
