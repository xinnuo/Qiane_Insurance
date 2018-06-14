package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.*
import java.util.ArrayList

class PointActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private lateinit var pointTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("我的积分", "积分规则")

        list.add(CommonData().apply {
            title = "2018-05-18 16:56"
            content = "分享成功"
            price = "10"
        })
        list.add(CommonData().apply {
            title = "2018-03-28 13:23"
            content = "分享成功"
            price = "10"
        })
        list.add(CommonData().apply {
            title = "2018-02-02 09:45"
            content = "分享成功"
            price = "10"
        })
        list.add(CommonData().apply {
            title = "2018-01-06 20:26"
            content = "分享成功"
            price = "10"
        })
        list.add(CommonData().apply {
            title = "2018-03-18 11:06"
            content = "注册成功"
            price = "30"
        })

        mAdapterEx.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(createView())
                .register<CommonData>(R.layout.item_income_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_income_time, data.title)
                            .text(R.id.item_income_content, data.content)
                            .text(R.id.item_income_money, "+${data.price}")

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

                themedTextView("积分明细", R.style.Font15_black) {
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dip(10), 0, 0, 0)
                }.lparams(width = matchParent, height = dip(40))

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

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
