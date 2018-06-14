package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.MultiGapDecoration
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.listeners.onClick
import java.util.ArrayList

class FilterCompanyActivity : BaseActivity() {

    private lateinit var mRecycler: RecyclerView
    private lateinit var clearBT: Button
    private lateinit var sureBT: Button
    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            mRecycler = recyclerView {
                overScrollMode = View.OVER_SCROLL_NEVER
            }.lparams(width = matchParent, height = dip(0)) {
                weight = 1f
            }

            view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))

            linearLayout {
                gravity = Gravity.END
                backgroundColorResource = R.color.white
                setPadding(dip(10), dip(5), dip(10), dip(5))

                clearBT = themedButton("清空", R.style.Font15_gray_borderless) {
                    backgroundResource = R.drawable.rec_bg_white_stroke_lighter
                }.lparams(width = dip(90), height = dip(35))

                sureBT = themedButton("确定", R.style.Font15_white_borderless) {
                    backgroundResource = R.drawable.rec_bg_red
                }.lparams(width = dip(90), height = dip(35)) {
                    leftMargin = dip(10)
                }

            }.lparams(width = matchParent)
        }
        init_title("公司")

        getData()
    }

    override fun init_title() {
        super.init_title()
        mRecycler.apply {
            layoutManager = GridLayoutManager(baseContext, 3)
            addItemDecoration(MultiGapDecoration(15).apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_company_grid) { data, injector ->
                    @Suppress("DEPRECATION")
                    injector.text(R.id.item_company, data.title)
                            .with<TextView>(R.id.item_company) {
                                if (data.isChecked) {
                                    it.setTextColor(resources.getColor(R.color.colorAccent))
                                    it.backgroundResource = R.mipmap.plan_option01
                                } else {
                                    it.setTextColor(resources.getColorStateList(R.color.item_selector_color))
                                    it.backgroundResource = R.drawable.item_selector
                                }
                            }

                            .clicked(R.id.item_company) {
                                data.isChecked = !data.isChecked
                                mAdapter.notifyItemChanged(list.indexOf(data))
                            }
                }
                .attachTo(mRecycler)

        clearBT.onClick {
            list.filter { it.isChecked }.forEach { it.isChecked = false }
            mAdapter.notifyDataSetChanged()
        }

        sureBT.onClick {
            ActivityStack.screenManager.popActivities(this::class.java)
        }
    }

    override fun getData() {
        list.add(CommonData().apply { title = "中国平安" })
        list.add(CommonData().apply { title = "太平洋保险" })
        list.add(CommonData().apply { title = "新华保险" })
        list.add(CommonData().apply { title = "中国太平" })
        list.add(CommonData().apply { title = "富德生命" })
        list.add(CommonData().apply { title = "人保寿" })
        list.add(CommonData().apply { title = "人保健康" })
        list.add(CommonData().apply { title = "阳光保险" })
        list.add(CommonData().apply { title = "合众人寿" })
        list.add(CommonData().apply { title = "天安人寿" })
        list.add(CommonData().apply { title = "信泰" })
        list.add(CommonData().apply { title = "恒大人寿" })
        list.add(CommonData().apply { title = "中英人寿" })
        list.add(CommonData().apply { title = "民生保险" })
        list.add(CommonData().apply { title = "华泰保险" })
        list.add(CommonData().apply { title = "农银人寿" })
        list.add(CommonData().apply { title = "君康人寿" })
        list.add(CommonData().apply { title = "国联人寿" })
        list.add(CommonData().apply { title = "前海人寿" })
        list.add(CommonData().apply { title = "安心保险" })
        list.add(CommonData().apply { title = "上海人寿" })

        mAdapter.updateData(list)
    }
}
