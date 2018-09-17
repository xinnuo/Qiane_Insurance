package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.MultiGapDecoration
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.listeners.onClick
import java.util.ArrayList

class FilterKindActivity : BaseActivity() {

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
        init_title("保险种类")

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
                    injector.text(R.id.item_company, data.insuranceTypeName)
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
                                list.filter {
                                    it is CommonData
                                            && it.isChecked
                                            && it.type == data.type
                                            && !data.isChecked
                                }.forEach { (it as CommonData).isChecked = false }
                                data.isChecked = !data.isChecked
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
                itemNames.add(it.insuranceTypeName)
            }

            EventBus.getDefault().post(RefreshMessageEvent(
                    "保险种类",
                    itemIds.joinToString(","),
                    itemNames.joinToString(",")))

            ActivityStack.screenManager.popActivities(this::class.java)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonModel>>>(BaseHttp.insurancetype_list_data)
                .tag(this@FilterKindActivity)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonModel>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonModel>>>) {

                        val items = ArrayList<CommonModel>()
                        items.addItems(response.body().`object`)

                        items.forEach { item ->
                            list.add(item.insuranceTypeName)

                            val datas = ArrayList<CommonData>()
                            datas.addItems(item.lcs)
                            datas.forEach { it.type = item.insuranceTypeName }
                            list.addAll(datas)
                        }

                        val insuranceTypeIds = intent.getStringExtra("id") ?: ""
                        val itemIds = insuranceTypeIds.split(",")
                        list.filter { it is CommonData }.forEach {
                            it as CommonData
                            if (itemIds.contains(it.insuranceTypeId)) it.isChecked = true
                        }

                        mAdapter.updateData(list)
                    }

                })
    }
}
