package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.MultiGapDecoration
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.util.*


class CompanyActivity : BaseActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init_title("选择公司")

        mRecyclerView = recyclerView {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(baseContext, 3)
            addItemDecoration(MultiGapDecoration(15).apply { isOffsetTopEnabled = true })
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_company_grid) { data, injector ->
                        @Suppress("DEPRECATION")
                        injector.text(R.id.item_company, data.title)
                                .with<TextView>(R.id.item_company) {
                                    if (data.isChecked){
                                        it.setTextColor(resources.getColor(R.color.colorAccent))
                                        it.backgroundResource = R.mipmap.plan_option01
                                    }
                                    else {
                                        it.setTextColor(resources.getColorStateList(R.color.item_selector_color))
                                        it.backgroundResource = R.drawable.item_selector
                                    }
                                }

                                .clicked(R.id.item_company) {
                                    list.filter { it.isChecked }.forEach { it.isChecked = false }
                                    data.isChecked = true
                                    (this.adapter as SlimAdapter).notifyDataSetChanged()
                                }
                    }
                    .attachTo(this)
        }

        list.add(CommonData().apply { title = "中国平安" })
        list.add(CommonData().apply { title = "太平洋保险" })
        list.add(CommonData().apply { title = "新华保险" })
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

        (mRecyclerView.adapter as SlimAdapter).updateData(list)
    }
}
