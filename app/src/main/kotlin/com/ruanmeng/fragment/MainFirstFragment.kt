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
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.qiane_insurance.NewsActivity
import com.ruanmeng.qiane_insurance.PlanDetailActivity
import com.ruanmeng.qiane_insurance.PlanMakeActivity
import com.ruanmeng.qiane_insurance.R
import com.ruanmeng.utils.DensityUtil
import com.ruanmeng.view.SwitcherTextView
import kotlinx.android.synthetic.main.fragment_main_first.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.support.v4.dip
import java.util.ArrayList

class MainFirstFragment : BaseFragment() {

    private lateinit var banner: RollPagerView
    private lateinit var mLoopAdapter: LoopAdapter
    private lateinit var mSwitchText: SwitcherTextView

    private val list = ArrayList<Any>()
    private val listSlider = ArrayList<CommonData>()
    private val listNotice = ArrayList<CommonData>()

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

        getData()
    }

    @SuppressLint("InflateParams")
    override fun init_title() {
        swipe_refresh.setProgressViewOffset(
                false,
                swipe_refresh.progressViewStartOffset + dip(60),
                swipe_refresh.progressViewEndOffset + dip(25))
        swipe_refresh.refresh { getData() }
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
                    mSwitchText = view.findViewById(R.id.first_notice)

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
                            .text(R.id.item_first_price, "${data.price}元")
                            .text(R.id.item_first_content, data.content)
                            .text(R.id.item_first_tui, "推广费${data.percent}%")

                            .with<ImageView>(R.id.item_first_img) {}

                            .visibility(R.id.item_first_price, if (data.price.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_tui, if (data.percent.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) {
                                if (data.type == "2") startActivity<PlanMakeActivity>()
                                else startActivity<PlanDetailActivity>()
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        listSlider.clear()
        listNotice.clear()
        list.clear()

        listSlider.add(CommonData().apply { sliderImg = "http://122.114.212.120:8097/upload/slider/370B45323DDA4661B2A80F2B395AA9F3.jpg" })
        listNotice.add(CommonData().apply { title = "为了孩子的健康，您选对保险了吗？" })
        list.add(CommonData().apply {
            title = "平安e生保2108版"
            content = "1万住院医疗+1万意外医疗+10万身价保"
            price = "228"
            percent = "20"
            type = "1"
        })
        list.add(CommonData().apply {
            title = "平安住院保"
            content = "30万身价+3万意外医疗+150元津贴"
            price = "189"
            percent = "15"
            type = "1"
        })
        list.add(CommonData().apply {
            title = "少儿平安福2018（至尊版）"
            content = "开门红客户回馈，免交1年保费"
            type = "2"
        })
        list.add(CommonData().apply {
            title = "平安意外险2018"
            content = "旗舰产品，强势升级"
            type = "2"
        })
        list.add(CommonData().apply {
            title = "少儿平安福2018"
            content = "凸显保障，期满领取保费"
            type = "2"
        })

        mAdapterEx.updateData(list)

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

        if (listNotice.size > 0) {
            mSwitchText.setData(listNotice, {
                startActivity<NewsActivity>()
            }, mSwitchText)
        }

        swipe_refresh.isRefreshing = false
    }
}
