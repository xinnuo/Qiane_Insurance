package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.view.DropPopWindow
import kotlinx.android.synthetic.main.header_income.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.include
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.verticalLayout
import java.util.*

class IncomeDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var startDate = ""
    private var endDate = ""
    private var profitType = ""

    private lateinit var dropPopWindowLeft: DropPopWindow
    private lateinit var dropPopWindowRight: DropPopWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            include<View>(R.layout.header_income)
            include<View>(R.layout.layout_list)
        }
        init_title("收入明细")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关收入明细！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_income_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_income_time, data.createDate)
                            .text(R.id.item_income_content, data.profitExplain)
                            .text(R.id.item_income_money, "+${data.profitSum}")

                            .visibility(R.id.item_income_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_income_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)

        dropPopWindowLeft = object : DropPopWindow(
                baseContext,
                R.layout.popu_layout_date,
                income_date_arrow) {

            override fun afterInitView(view: View) {
                val popStart = view.findViewById<TextView>(R.id.pop_start)
                val popEnd = view.findViewById<TextView>(R.id.pop_end)
                val popFilter = view.findViewById<TextView>(R.id.pop_filter)
                val popReset = view.findViewById<TextView>(R.id.pop_reset)
                val yearNow = Calendar.getInstance().get(Calendar.YEAR)

                popStart.setOnClickListener {

                    DialogHelper.showDateDialog(baseContext,
                            yearNow - 50,
                            yearNow,
                            3,
                            "选择起始日期",
                            true,
                            false) { _, _, _, _, _, date ->

                        if (popEnd.text.isNotEmpty()) {
                            val days = TimeHelper.getInstance().getDays(date, popEnd.text.toString())
                            if (days < 0) {
                                showToast("起始日期不能大于结束日期")
                                return@showDateDialog
                            }
                        }

                        popStart.text = date
                    }
                }

                popEnd.setOnClickListener {

                    DialogHelper.showDateDialog(baseContext,
                            yearNow - 50,
                            yearNow,
                            3,
                            "选择结束日期",
                            true,
                            false) { _, _, _, _, _, date ->
                        if (popStart.text.isNotEmpty()) {
                            val days = TimeHelper.getInstance().getDays(popStart.text.toString(), date)
                            if (days < 0) {
                                showToast("结束日期不能小于起始日期")
                                return@showDateDialog
                            }
                        }

                        popEnd.text = date
                    }
                }

                popFilter.setOnClickListener {
                    if (popStart.text.isNotEmpty() && popEnd.text.isEmpty()) {
                        showToast("请选择结束日期")
                        return@setOnClickListener
                    }

                    if (popStart.text.isEmpty() && popEnd.text.isNotEmpty()) {
                        showToast("请选择起始日期")
                        return@setOnClickListener
                    }

                    if (popStart.text.isNotEmpty() && popEnd.text.isNotEmpty()) {
                        startDate = popStart.text.toString()
                        endDate = popEnd.text.toString()

                        dismiss()
                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }
                }

                popReset.setOnClickListener {
                    popStart.text = ""
                    popEnd.text = ""

                    if (startDate.isNotEmpty()
                            || endDate.isNotEmpty()) {
                        startDate = ""
                        endDate = ""

                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }

                    dismiss()
                }
            }

        }

        dropPopWindowRight = object : DropPopWindow(
                baseContext,
                R.layout.popu_layout_money,
                income_money_arrow) {

            override fun afterInitView(view: View) {
                val popAll = view.findViewById<TextView>(R.id.pop_all)
                val popDir = view.findViewById<TextView>(R.id.pop_dir)
                val popRec = view.findViewById<TextView>(R.id.pop_rec)

                popAll.onClick {
                    profitType = ""
                    income_money_hint.text = "全部收益"

                    dismiss()
                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                }

                popDir.onClick {
                    profitType = "1"
                    income_money_hint.text = "直接收益"

                    dismiss()
                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                }

                popRec.onClick {
                    profitType = "2"
                    income_money_hint.text = "推荐人收益"

                    dismiss()
                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                }
            }
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.income_date -> {
                if (dropPopWindowLeft.isShowing) dropPopWindowLeft.dismiss()
                else dropPopWindowLeft.showAsDropDown(income_divider)
            }
            R.id.income_money -> {
                if (dropPopWindowRight.isShowing) dropPopWindowRight.dismiss()
                else dropPopWindowRight.showAsDropDown(income_divider)
            }
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.profit_list_data)
                .tag(this@IncomeDetailActivity)
                .headers("token", getString("token"))
                .params("startDate", startDate)
                .params("endDate", endDate)
                .params("profitType", profitType)
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

    fun updateList() {
        OkGo.getInstance().cancelTag(this@IncomeDetailActivity)
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
