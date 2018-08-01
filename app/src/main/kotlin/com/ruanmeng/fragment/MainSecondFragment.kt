package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.qiane_insurance.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.util.*

class MainSecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private var insuranceTypeIds = ""
    private var companyId = ""
    private var age = ""
    private var sorttype = ""

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainSecondFragment)

        swipe_refresh.isRefreshing = true
        getData()
        getData(pageNum)
    }

    override fun init_title() {
        empty_hint.text = "暂无相关产品信息！"
        swipe_refresh.refresh {
            getData()
            getData(1)
        }
        recycle_list.load_Linear(activity!!, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_first_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_first_name, data.productName)
                            .text(R.id.item_first_price, "${data.productSum}元")
                            .text(R.id.item_first_content, data.synopsis)
                            .text(R.id.item_first_tui, "推广费${(data.recommendSum.toDouble() * 100).toInt()}%")

                            .with<ImageView>(R.id.item_first_img) {
                                it.setImageURL(BaseHttp.baseImg + data.productImg)
                            }

                            .visibility(R.id.item_first_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) {
                                startActivity<PlanLookActivity>(
                                        "productinId" to data.productinId,
                                        "type" to "产品详情",
                                        "outHref" to data.outHref)
                            }
                }
                .attachTo(recycle_list)

        second_type_ll.onClick    { startActivity<FilterKindActivity>("id" to insuranceTypeIds) }
        second_company_ll.onClick { startActivity<FilterCompanyActivity>("companyId" to companyId) }
        second_age_ll.onClick     { startActivity<FilterAgeActivity>("ageId" to age) }
        second_filter_ll.onClick  { startActivity<FilterActivity>("id" to sorttype) }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.read_user_ctn_data)
                .tag(this@MainSecondFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(activity, false) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()
                        second_total.text = obj.optStringNotEmpty("userCtn", "0")
                        second_read.text = obj.optStringNotEmpty("readCtn", "0")
                    }

                })
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.product_list_data)
                .tag(this@MainSecondFragment)
                .params("insuranceTypeIds", insuranceTypeIds)
                .params("companyId", companyId)
                .params("age", age)
                .params("sorttype", sorttype)
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

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainSecondFragment)
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "保险种类" -> {
                insuranceTypeIds = event.id

                if (insuranceTypeIds.isEmpty()) {
                    second_type.text = "保险种类"
                    second_type.textColor = resources.getColor(R.color.gray)
                    second_type_arrow.setImageResource(R.mipmap.triangle_black)
                } else {
                    second_type.text = event.name
                    second_type.textColor = resources.getColor(R.color.colorAccent)
                    second_type_arrow.setImageResource(R.mipmap.triangle_red)
                }

                updateList()
            }
            "公司" -> {
                companyId = event.id

                if (companyId.isEmpty()) {
                    second_company.text = "公司"
                    second_company.textColor = resources.getColor(R.color.gray)
                    second_company_arrow.setImageResource(R.mipmap.triangle_black)
                } else {
                    second_company.text = event.name
                    second_company.textColor = resources.getColor(R.color.colorAccent)
                    second_company_arrow.setImageResource(R.mipmap.triangle_red)
                }

                updateList()
            }
            "年龄" -> {
                age = event.id

                if (age.isEmpty()) {
                    second_age.text = "年龄"
                    second_age.textColor = resources.getColor(R.color.gray)
                    second_age_arrow.setImageResource(R.mipmap.triangle_black)
                } else {
                    second_age.text = event.name
                    second_age.textColor = resources.getColor(R.color.colorAccent)
                    second_age_arrow.setImageResource(R.mipmap.triangle_red)
                }

                updateList()
            }
            "筛选" -> {
                sorttype = event.id

                if (sorttype.isEmpty()) {
                    second_filter.text = "筛选"
                    second_filter.textColor = resources.getColor(R.color.gray)
                    second_filter_arrow.setImageResource(R.mipmap.money_tab_icon01)
                } else {
                    second_filter.text = event.name
                    second_filter.textColor = resources.getColor(R.color.colorAccent)
                    second_filter_arrow.setImageResource(R.mipmap.money_tab_icon02)
                }

                updateList()
            }
            "更新客户", "删除客户" -> getData()
        }
    }
}
