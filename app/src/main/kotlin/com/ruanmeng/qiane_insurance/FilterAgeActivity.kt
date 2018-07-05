package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.gone
import com.ruanmeng.base.visible
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.MultiGapDecoration
import kotlinx.android.synthetic.main.activity_filter_age.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.backgroundResource
import java.util.*

class FilterAgeActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private var ageId = ""
    private var ageName = "年龄"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_age)
        init_title("年龄")

        list.add(CommonData().apply { title = "不限年龄" })
        list.add(CommonData().apply {
            title = "0-17周岁"
            type = "0,17"
        })
        list.add(CommonData().apply {
            title = "18-65周岁"
            type = "18,65"
        })
        list.add(CommonData().apply {
            title = "66-100周岁"
            type = "66,100"
        })
        list.add(CommonData().apply {
            title = "自定义年龄"
            type = "-1"
        })

        val ageId = intent.getStringExtra("ageId") ?: ""
        if (ageId.isEmpty()) list.find { it.type.isEmpty() }?.isChecked = true
        else {
            if (list.none { it.type == ageId }) {
                list.find { it.type == "-1" }?.isChecked = true
                age_custom.visible()
                age_num.text = "${ageId.split(",")[0]}周岁"
            } else {
                list.forEach { if (it.type == ageId) it.isChecked = true }
            }
        }
        when {
            ageId.isEmpty() -> list.find { it.type.isEmpty() }?.isChecked = true
            ageId == "-1" -> list.find { it.type == "-1" }?.isChecked = true
            else -> list.forEach { if (it.type == ageId) it.isChecked = true }
        }

        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        age_list.apply {
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
                                list.filter { it.isChecked }.forEach { it.isChecked = false }
                                data.isChecked = true
                                mAdapter.notifyDataSetChanged()

                                if (data.title == "自定义年龄") age_custom.visible()
                                else {
                                    age_num.text = "0周岁"
                                    age_custom.gone()
                                }
                            }
                }
                .attachTo(age_list)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.age_num_ll -> {
                val items = ArrayList<String>()
                (0 until 101).forEach { items.add("${it}周岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        items) { _, name ->
                    age_num.text = name
                }
            }
            R.id.age_birth -> {
                val yearNow = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        yearNow - 70,
                        yearNow, 3,
                        "选择生日",
                        true, false) { year, _, _, _, _, _ ->
                    age_num.text = "${yearNow - year}周岁"
                }
            }
            R.id.age_clear -> {
                list.filter { it.isChecked }.forEach { it.isChecked = false }
                mAdapter.notifyDataSetChanged()

                age_num.text = "0周岁"
                age_custom.gone()
            }
            R.id.age_sure -> {
                val data = list.firstOrNull { it.isChecked }
                when {
                    data == null || data.type.isEmpty() -> {
                        ageId = ""
                        ageName = "不限年龄"
                    }
                    data.type == "-1" -> {
                        val hint = age_num.text.toString().replace("周岁", "")
                        ageId = "$hint,$hint"
                        ageName = age_num.text.toString()
                    }
                    else -> {
                        ageId = data.type
                        ageName = data.title
                    }
                }

                EventBus.getDefault().post(RefreshMessageEvent("年龄", ageId, ageName))

                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }
}
