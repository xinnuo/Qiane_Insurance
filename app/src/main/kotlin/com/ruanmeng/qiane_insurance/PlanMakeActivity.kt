package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_plan_make.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.startActivity
import win.smartown.android.library.tableLayout.TableAdapter
import java.util.*
import kotlin.collections.ArrayList

class PlanMakeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_make)
        init_title("计划书")
    }

    override fun init_title() {
        super.init_title()
        plan_select.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) plan_expand.expand()
            else plan_expand.collapse()
        }
        plane_select.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) plane_expand.expand()
            else plane_expand.collapse()
        }

        planed_check.check(R.id.planed_check1)
        plan_check.check(R.id.plan_check1)
        plane_check.check(R.id.plane_check1)
        planed_check.check(R.id.planed_check1)
        planer_check.check(R.id.planer_check1)

        plan_title_ll.gone()
        plan_append.visible()
        val contentList = ArrayList<Array<String>>()
        contentList.add(arrayOf("险种", "保额", "保费", "交费期间"))
        contentList.add(arrayOf("平安福2018", "310000.00", "4289.00", "30年交"))
        contentList.add(arrayOf("平安福2018", "300000.00", "3600.00", "30年交"))
        plan_table.setAdapter(object : TableAdapter {

            override fun getColumnCount(): Int = contentList[0].size

            override fun getColumnContent(position: Int): Array<String?> {
                val items = arrayOfNulls<String>(contentList.size)
                (0 until contentList.size).forEach { items[it] = contentList[it][position] }
                return items
            }

        })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_data -> startActivity<PlanDetailActivity>()
            R.id.plan_make -> startActivity<PlanDoneActivity>()
            R.id.planed_age_ll -> {
                val items = ArrayList<String>()
                (1 until 71).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        items) { _, name ->
                    planed_age.text = name
                }
            }
            R.id.plan_age_ll -> {
                val items = ArrayList<String>()
                (1 until 71).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        items) { _, name ->
                    plan_age.text = name
                }
            }
            R.id.plane_age_ll -> {
                val items = ArrayList<String>()
                (1 until 71).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        items) { _, name ->
                    plane_age.text = name
                }
            }
            R.id.planed_birth -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - 70,
                        year, 3,
                        "选择生日",
                        true, false) { _, _, _, _, _, date ->
                    planed_birth.text = date
                }
            }
            R.id.plan_birth -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - 70,
                        year, 3,
                        "选择生日",
                        true, false) { _, _, _, _, _, date ->
                    plan_birth.text = date
                }
            }
            R.id.plane_birth -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - 70,
                        year, 3,
                        "选择生日",
                        true, false) { _, _, _, _, _, date ->
                    plane_birth.text = date
                }
            }
            R.id.plan_host -> {
                val dialog = object : BottomDialog(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onCreateView(): View {
                        val view = View.inflate(mContext, R.layout.dialog_plan_host, null)
                        val dialogTitle = view.findViewById<TextView>(R.id.dialog_plan_title)
                        val dialogClose = view.findViewById<ImageView>(R.id.dialog_plan_close)
                        val dialogAgeLL = view.findViewById<LinearLayout>(R.id.dialog_plan_age_ll)
                        val dialogAge = view.findViewById<TextView>(R.id.dialog_plan_age)
                        val dialogYearLL = view.findViewById<LinearLayout>(R.id.dialog_plan_year_ll)
                        val dialogYear = view.findViewById<TextView>(R.id.dialog_plan_year)
                        val dialogMoney = view.findViewById<EditText>(R.id.dialog_plan_money)
                        val dialogMoneyClear = view.findViewById<ImageView>(R.id.dialog_plan_money_clear)
                        val dialogFee = view.findViewById<EditText>(R.id.dialog_plan_fee)
                        val dialogFeeClear = view.findViewById<ImageView>(R.id.dialog_plan_fee_clear)
                        val dialogList = view.findViewById<RecyclerView>(R.id.dialog_plan_list)
                        val dialogOk = view.findViewById<Button>(R.id.dialog_plan_ok)

                        val items = ArrayList<CommonData>()
                        items.add(CommonData().apply {
                            title = "平安福2018肿瘤"
                            isChecked = true
                        })
                        items.add(CommonData().apply {
                            title = "平安福2018重疾"
                            isChecked = true
                        })
                        items.add(CommonData().apply {
                            title = "投保人豁免"
                            isClickable = false
                        })
                        dialogList.apply {
                            isNestedScrollingEnabled = false
                            layoutManager = FullyLinearLayoutManager(baseContext)
                            adapter = SlimAdapter.create()
                                    .register<CommonData>(R.layout.item_plan_dialog_list) { data, injector ->
                                        val isLast = items.indexOf(data) == items.size - 1
                                        @Suppress("DEPRECATION")
                                        injector.text(R.id.item_plan_check, data.title)
                                                .textColor(R.id.item_plan_check, resources.getColor(if (data.isClickable) R.color.black else R.color.light))
                                                .checked(R.id.item_plan_check, data.isChecked)
                                                .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                                                .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)

                                                .clicked(R.id.item_plan) {
                                                    if (data.isClickable) {
                                                        data.isChecked = !data.isChecked
                                                        (this.adapter as SlimAdapter).notifyDataSetChanged()
                                                    }
                                                }
                                    }
                                    .attachTo(this)
                        }
                        (dialogList.adapter as SlimAdapter).updateData(items)

                        dialogTitle.text = "平安福2018"
                        dialogClose.setOnClickListener { dismiss() }
                        dialogAgeLL.setOnClickListener {
                            DialogHelper.showItemDialog(
                                    baseContext,
                                    "保险期间",
                                    listOf("至60岁", "至65岁", "至70岁", "至75岁", "至80岁")) { _, name ->
                                dialogAge.text = name
                            }
                        }
                        dialogYearLL.setOnClickListener {
                            DialogHelper.showItemDialog(
                                    baseContext,
                                    "交费期间",
                                    listOf("趸交", "3年交", "5年交", "10年交", "15年交", "20年交", "30年交")) { _, name ->
                                dialogYear.text = name
                            }
                        }
                        dialogMoneyClear.setOnClickListener { dialogMoney.setText("") }
                        dialogFeeClear.setOnClickListener { dialogFee.setText("") }
                        dialogOk.setOnClickListener { dismiss() }

                        return view
                    }

                }
                dialog.show()
            }
        }
    }
}
