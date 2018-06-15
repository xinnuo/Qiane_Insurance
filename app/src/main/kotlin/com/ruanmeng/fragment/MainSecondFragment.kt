package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.qiane_insurance.PlanDetailActivity
import com.ruanmeng.qiane_insurance.R
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class MainSecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

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

        getData()
    }

    override fun init_title() {
        swipe_refresh.refresh { getData() }
        activity?.let { recycle_list.load_Linear(it, swipe_refresh) }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_first_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_first_name, data.title)
                            .text(R.id.item_first_price, "${data.price}元")
                            .text(R.id.item_first_content, data.content)
                            .text(R.id.item_first_tui, "推广费${data.percent}%")

                            .with<ImageView>(R.id.item_first_img) {}

                            .visibility(R.id.item_first_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) {
                                startActivity<PlanDetailActivity>()
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        list.clear()

        list.add(CommonData().apply {
            title = "平安e生保2108版"
            content = "1万住院医疗+1万意外医疗+10万身价保障"
            price = "228"
            percent = "20"
        })
        list.add(CommonData().apply {
            title = "平安住院保"
            content = "30万身价+3万意外医疗+150元津贴"
            price = "189"
            percent = "15"
        })
        list.add(CommonData().apply {
            title = "少儿平安福2018（至尊版）"
            content = "1万住院医疗+1万意外医疗+10万身价保障"
            price = "176"
            percent = "15"
        })
        list.add(CommonData().apply {
            title = "平安意外险2018"
            content = "30万身价+3万意外医疗+150元津贴"
            price = "168"
            percent = "16"
        })
        list.add(CommonData().apply {
            title = "平安e生保2108版"
            content = "1万住院医疗+1万意外医疗+10万身价保障"
            price = "200"
            percent = "20"
        })
        list.add(CommonData().apply {
            title = "平安住院保"
            content = "30万身价+3万意外医疗+150元津贴"
            price = "100"
            percent = "15"
        })
        list.add(CommonData().apply {
            title = "少儿平安福2018（至尊版）"
            content = "1万住院医疗+1万意外医疗+10万身价保障"
            price = "300"
            percent = "15"
        })
        list.add(CommonData().apply {
            title = "平安意外险2018"
            content = "30万身价+3万意外医疗+150元津贴"
            price = "150"
            percent = "16"
        })

        mAdapter.updateData(list)

        swipe_refresh.isRefreshing = false
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
}
