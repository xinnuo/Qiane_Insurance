package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.ArrayList

class PointActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private lateinit var pointTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("我的积分", "积分规则")

        swipe_refresh.isRefreshing = false
        getData()
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(createView())
                .register<CommonData>(R.layout.item_income_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_income_time, data.createDate)
                            .text(R.id.item_income_content, data.integralExplain)
                            .text(R.id.item_income_money, "+${data.integralScore}")

                            .visibility(R.id.item_income_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_income_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    private fun createView(): View {
        return UI {
            verticalLayout {
                lparams(height = wrapContent)
                gravity = Gravity.CENTER_HORIZONTAL
                backgroundColorResource = R.color.white

                imageView {
                    imageResource = R.mipmap.income_icon03
                }.lparams(width = dip(65),
                        height = dip(65)) {
                    topMargin = dip(40)
                }

                themedTextView("当前积分", R.style.Font13_gray)
                        .lparams(width = wrapContent) {
                            topMargin = dip(10)
                        }

                pointTV = themedTextView("0", R.style.Font15_black_medium) {
                    textSize = 30f
                }.lparams(width = wrapContent) {
                    topMargin = dip(10)
                    bottomMargin = dip(40)
                }

                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))
                view { backgroundColorResource = R.color.background }.lparams(height = dip(10))
                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))

                linearLayout {
                    lparams(width = matchParent, height = dip(40))

                    themedTextView("积分明细", R.style.Font15_black) {
                        gravity = Gravity.CENTER_VERTICAL
                    }.lparams(height = matchParent) {
                        marginStart = dip(10)
                        weight = 1f
                    }

                    themedTextView("积分任务", R.style.Font15_black) {
                        gravity = Gravity.CENTER_VERTICAL
                        setPadding(dip(10), dip(0), dip(10), dip(0))
                        onClick { startActivity<PointListActivity>() }
                    }.lparams(height = matchParent)
                }

                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))
            }
        }.view
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<WebActivity>("title" to "积分规则")
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.integral_info)
                .tag(this@PointActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()
                        val integral = obj.optStringNotEmpty("integral", "0")
                        pointTV.text = DecimalFormat("0.##").format(integral.toDouble())
                    }

                })
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.integral_list_data)
                .tag(this@PointActivity)
                .headers("token", getString("token"))
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
