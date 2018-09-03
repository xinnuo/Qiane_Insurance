package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jude.rollviewpager.RollPagerView
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.qiane_insurance.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_first.*
import kotlinx.android.synthetic.main.header_first.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import java.util.*

class MainFirstFragment : BaseFragment() {

    private lateinit var banner: RollPagerView
    private lateinit var mLoopAdapter: LoopAdapter

    private val list = ArrayList<Any>()
    private val listSlider = ArrayList<CommonData>()
    private val listNotice = ArrayList<CommonData>()
    private var companyId = ""

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainFirstFragment)

        swipe_refresh.isRefreshing = true
        getCompanyData()
        getHeaderData()
        getData()
    }

    @SuppressLint("InflateParams")
    override fun init_title() {
        companyId = getString("companyId")

        swipe_refresh.setProgressViewOffset(
                false,
                swipe_refresh.progressViewStartOffset + dip(60),
                swipe_refresh.progressViewEndOffset + dip(25))
        swipe_refresh.refresh {
            getHeaderData()
            getData()
        }
        recycle_list.load_Linear(activity!!, swipe_refresh)

        recycle_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private val height = dip(100)
            private var overallXScroll = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                overallXScroll += dy // dy:每一次竖直滑动增量 向下为正 向上为负

                when {
                    overallXScroll <= 0 -> {
                        first_header.setBackgroundColor(Color.argb(0, 194, 13, 35))
                        // first_message_dot.setImageResource(R.drawable.orange_oval)
                    }
                    overallXScroll in 1..height -> {
                        val alpha = 255 * overallXScroll / height
                        first_header.setBackgroundColor(Color.argb(alpha, 194, 13, 35))
                    }
                    else -> {
                        first_header.setBackgroundColor(Color.argb(255, 194, 13, 35))
                        // first_message_dot.setImageResource(R.drawable.white_oval)
                    }
                }
            }
        })

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(activity).inflate(R.layout.header_first, null)
                    banner = view.findViewById(R.id.first_banner)

                    mLoopAdapter = LoopAdapter(activity, banner)
                    banner.apply {
                        setAdapter(mLoopAdapter)
                        setOnItemClickListener { /*轮播图点击事件*/ }
                    }
                    addHeader(view)
                }
                .register<CommonData>(R.layout.item_first_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_first_name, data.title)
                            .text(R.id.item_first_price, "${data.productSum}元")
                            .text(R.id.item_first_content, data.synopsis)
                            .text(R.id.item_first_tui,
                                    "推广费${if (data.recommendSum.isEmpty()) 0 else (data.recommendSum.toDouble() * 100).toInt()}%")

                            .with<ImageView>(R.id.item_first_img) {
                                it.setImageURL(BaseHttp.baseImg + data.img)
                            }

                            .visibility(R.id.item_first_price, if (data.type == "1") View.GONE else View.VISIBLE)
                            // .visibility(R.id.item_first_tui, if (data.type == "1") View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) {
                                if (data.type == "1")
                                    startActivity<PlanMakeActivity>(
                                            "title" to data.title,
                                            "prospectusId" to data.productprospectusId)
                                else {
                                    startActivity<PlanLookActivity>(
                                            "productinId" to data.productprospectusId,
                                            "type" to "产品详情",
                                            "outHref" to data.outHref)
                                }
                            }
                }
                .attachTo(recycle_list)
    }

    private fun getHeaderData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.index_data)
                .tag(this@MainFirstFragment)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val obj = response.body().`object` ?: return
                        listSlider.apply {
                            clear()
                            addItems(obj.slider)
                        }

                        listNotice.apply {
                            clear()
                            addItems(obj.news)
                        }

                        val imgs = ArrayList<String>()
                        listSlider.mapTo(imgs) { it.sliderImg }
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size < 2) {
                            banner.pause()
                            banner.setHintViewVisibility(false)
                        } else {
                            banner.resume()
                            banner.setHintViewVisibility(true)
                        }

                        if (listNotice.isNotEmpty()) {
                            first_notice.setData(listNotice, {
                                startActivity<NewsActivity>()
                            }, first_notice)
                        }
                    }

                })
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.index_productprospectus)
                .tag(this@MainFirstFragment)
                .params("companyId", companyId)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
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

    private fun getCompanyData() {
        OkGo.post<String>(BaseHttp.user_profession_info)
                .tag(this@MainFirstFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(activity, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object")
                                ?: JSONObject()

                        putString("companyName", obj.optString("companyName"))
                        first_company_name.text = getString("companyName")
                    }

                })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainFirstFragment)
        super.onDestroy()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "选择公司", "更新公司" -> {
                companyId = event.id
                first_company_name.text = event.name

                swipe_refresh.isRefreshing = true
                if (list.isNotEmpty()) {
                    list.clear()
                    mAdapterEx.notifyDataSetChanged()
                }

                getHeaderData()
                getData()
            }
        }
    }
}
