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
                    injector.text(R.id.item_company, data.companyName)
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
            val itemIds = ArrayList<String>()
            val itemNames = ArrayList<String>()

            list.filter { it.isChecked }.forEach {
                itemIds.add(it.companyId)
                itemNames.add(it.companyName)
            }

            EventBus.getDefault().post(RefreshMessageEvent(
                    "公司",
                    itemIds.joinToString(","),
                    itemNames.joinToString(",")))

            ActivityStack.screenManager.popActivities(this::class.java)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.company_list_data)
                .tag(this@FilterCompanyActivity)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        val companyId = intent.getStringExtra("companyId") ?: ""
                        val itemIds = companyId.split(",")
                        list.forEach {
                            if (itemIds.contains(it.companyId)) it.isChecked = true
                        }

                        mAdapter.updateData(list)
                    }

                })
    }
}
