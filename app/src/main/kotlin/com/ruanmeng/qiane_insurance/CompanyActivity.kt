package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
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
                                    list.filter { it.isChecked }.forEach { it.isChecked = false }
                                    data.isChecked = true
                                    (this.adapter as SlimAdapter).notifyDataSetChanged()

                                    EventBus.getDefault().post(RefreshMessageEvent(
                                            intent.getStringExtra("type"),
                                            data.companyId,
                                            data.companyName))

                                    window.decorView.postDelayed({
                                        ActivityStack.screenManager.popActivities(this@CompanyActivity::class.java)
                                    }, 300)
                                }
                    }
                    .attachTo(this)
        }

        getData()
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.company_list_data)
                .tag(this@CompanyActivity)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        val companyId = intent.getStringExtra("companyId") ?: ""
                        if (list.any { it.companyId == companyId }) {
                            list.first { it.companyId == companyId }.isChecked = true
                        }

                        (mRecyclerView.adapter as SlimAdapter).updateData(list)
                    }

                })
    }
}
