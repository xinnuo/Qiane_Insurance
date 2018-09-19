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
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.MultiGapDecoration
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.listeners.onClick
import java.util.ArrayList

class FilterActivity : BaseActivity() {

    private lateinit var mRecycler: RecyclerView
    private lateinit var clearBT: Button
    private lateinit var sureBT: Button
    private val list = ArrayList<Any>()

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
        init_title("筛选")

        getData()
    }

    override fun init_title() {
        super.init_title()
        mRecycler.apply {
            layoutManager = GridLayoutManager(baseContext, 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = when {
                        mAdapter.getItem(position) is CommonData -> 1
                        else -> 3
                    }
                }
            }
            addItemDecoration(MultiGapDecoration(15).apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_filter_header) { data, injector ->
                    injector.text(R.id.item_header, data)
                            .visibility(R.id.item_header_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)
                }
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

                            .clicked(R.id.item_company) { _ ->
                                list.filter { it is CommonData && it.isChecked && it.type == data.type }
                                        .forEach { (it as CommonData).isChecked = false }
                                data.isChecked = true
                                mAdapter.notifyDataSetChanged()
                            }
                }
                .attachTo(mRecycler)

        clearBT.onClick { _ ->
            list.filter { it is CommonData && it.isChecked }
                    .forEach { (it as CommonData).isChecked = false }
            mAdapter.notifyDataSetChanged()
        }

        sureBT.onClick { _ ->
            val itemIds = ArrayList<String>()
            val itemNames = ArrayList<String>()

            list.filter { it is CommonData && it.isChecked }.forEach {
                it as CommonData
                itemIds.add(it.insuranceTypeId)
                itemNames.add(it.title)
            }

            EventBus.getDefault().post(RefreshMessageEvent(
                    "筛选",
                    itemIds.joinToString(","),
                    itemNames.joinToString(",")))

            ActivityStack.screenManager.popActivities(this::class.java)
        }
    }

    override fun getData() {
        list.add("排序")
        list.add(CommonData().apply {
            title = "默认排序"
            type = "排序"
            insuranceTypeId = "0"
        })
        list.add(CommonData().apply {
            title = "推广费优先"
            type = "排序"
            insuranceTypeId = "1"
        })

        val insuranceTypeIds = intent.getStringExtra("id") ?: ""
        val itemIds = insuranceTypeIds.split(",")
        list.filter { it is CommonData }.forEach {
            it as CommonData
            if (itemIds.contains(it.insuranceTypeId)) it.isChecked = true
        }

        mAdapter.updateData(list)
    }
}
