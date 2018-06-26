package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_plan_make.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.sdk25.listeners.onCheckedChange
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlanMakeActivity : BaseActivity() {

    private val listKind = ArrayList<CommonData>()
    private val listChecked = ArrayList<InsuranceData>()
    private val mapKind = HashMap<String, List<InsuranceModel>>()
    private var mPlanedSex = 1
    private var mPlanedYear: Int = 0
    private var mPlanedMonth: Int = 0
    private var mPlanedDay: Int = 0


    private var mPlanSex = 1
    private var mPlaneSex = 1
    private var mPlanerSex = 1

    private var mStartAge = 0
    private var mMedianAge = 0
    private var mEndAge = 0
    private var mCheckAge = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_make)
        init_title("计划书")

        getData()
    }

    override fun init_title() {
        super.init_title()
        mPlanedYear = Calendar.getInstance().get(Calendar.YEAR)
        mPlanedMonth = Calendar.getInstance().get(Calendar.MONTH)
        mPlanedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        plan_select.onCheckedChange { _, isChecked ->
            if (isChecked) plan_expand.expand()
            else plan_expand.collapse()
        }

        plane_select.onCheckedChange { _, isChecked ->
            if (isChecked) plane_expand.expand()
            else plane_expand.collapse()
        }

        planed_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.planed_check1 -> mPlanedSex = 1
                R.id.planed_check2 -> mPlanedSex = 0
            }
        }

        plan_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.plan_check1 -> mPlanSex = 1
                R.id.plan_check2 -> mPlanSex = 0
            }
        }

        plane_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.plane_check1 -> mPlaneSex = 1
                R.id.plane_check2 -> mPlaneSex = 0
            }
        }

        planer_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.planer_check1 -> mPlanerSex = 1
                R.id.planer_check2 -> mPlanerSex = 0
            }
        }

        planed_check.check(R.id.planed_check1)
        plan_check.check(R.id.plan_check1)
        plane_check.check(R.id.plane_check1)
        planer_check.check(R.id.planer_check1)

        plan_title.text = intent.getStringExtra("title")
        plan_title_ll.visible() //主险
        plan_append.gone()      //附加险

        plan_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<InsuranceData>(R.layout.item_plan_make_list) { data, injector ->
                        injector.text(R.id.item_plan_title, data.insuranceKindName)
                                .text(R.id.item_plan_total,
                                        "首年保费：${DecimalFormat("0.00").format(data.insuredFee)}")
                                .visibility(R.id.item_plan_del, if (data.isDelete) View.VISIBLE else View.GONE)

                                .with<RecyclerView>(R.id.item_plan_table) {
                                    it.apply {
                                        isNestedScrollingEnabled = false
                                        layoutManager = FullyLinearLayoutManager(baseContext)
                                        adapter = SlimAdapter.create()
                                                .register<TableData>(R.layout.item_plan_table_list) { item, injector ->
                                                    val isLast = data.listTable.indexOf(item) == data.listTable.size - 1

                                                    injector.text(R.id.item_plan_column1, item.name)
                                                            .text(R.id.item_plan_column2, item.amount)
                                                            .text(R.id.item_plan_column3, item.fee)
                                                            .text(R.id.item_plan_column4, item.range)
                                                            .visibility(R.id.item_plan_divider, if (isLast) View.GONE else View.VISIBLE)
                                                }
                                                .attachTo(this)

                                        (it.adapter as SlimAdapter).updateData(data.listTable)
                                    }
                                }
                    }
                    .attachTo(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_data -> startActivity<PlanDetailActivity>()
            R.id.plan_make -> startActivity<PlanDoneActivity>()
            R.id.planed_age_ll -> {
                val items = ArrayList<String>()
                (mStartAge until mEndAge + 1).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        mCheckAge - mStartAge,
                        items) { position, name ->
                    mCheckAge = mStartAge + position
                    planed_age.text = name

                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    mPlanedYear = year - mStartAge - position
                    planed_birth.text = "生日"
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
                        year - mEndAge,
                        year - mStartAge,
                        "选择生日",
                        mPlanedYear,
                        mPlanedMonth,
                        mPlanedDay,
                        false) { y, m, d, _, _, date ->
                    mPlanedYear = y
                    mPlanedMonth = m - 1
                    mPlanedDay = d

                    mCheckAge = year - y
                    planed_age.text = "${mCheckAge}岁"
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
                        val dialogList = view.findViewById<RecyclerView>(R.id.dialog_plan_list)
                        val dialogOk = view.findViewById<Button>(R.id.dialog_plan_ok)

                        val itemLbs = ArrayList<InsuranceModel>()
                        itemLbs.addItems(mapKind[listKind[0].insuranceKindId])


                        dialogList.apply {
                            isNestedScrollingEnabled = false
                            layoutManager = FullyLinearLayoutManager(baseContext)
                            adapter = SlimAdapter.create()
                                    .register<InsuranceModel>(R.layout.item_plan_dialog_list) { data, injector ->
                                        val isFirst = itemLbs.indexOf(data) == 0
                                        val isLast = itemLbs.indexOf(data) == itemLbs.size - 1
                                        @Suppress("DEPRECATION")
                                        injector.text(R.id.item_plan_check, data.insuranceKindName)
                                                .textColor(R.id.item_plan_check, resources.getColor(if (data.isClickable) R.color.black else R.color.light))
                                                .checked(R.id.item_plan_check, data.isChecked)
                                                .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                                                .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)
                                                .visibility(R.id.item_plan_divider3, if (!isFirst) View.GONE else View.VISIBLE)

                                                .with<LinearLayout>(R.id.item_plan_container) {
                                                    it.removeAllViews()
                                                    val itemLds = ArrayList<CommonData>()
                                                    itemLds.addItems(data.lds)
                                                    itemLds.forEach { item ->
                                                        when (item.type) {
                                                            "0" -> {
                                                                val view = View.inflate(baseContext, R.layout.item_plan_dialog_type1_list, null)
                                                                val tvName = view.findViewById<TextView>(R.id.dialog_type_name)
                                                                val tvAge = view.findViewById<TextView>(R.id.dialog_type_age)

                                                                tvName.text = item.optDictionaryName
                                                                it.addView(view)
                                                            }
                                                            "1" -> {
                                                                val view = View.inflate(baseContext, R.layout.item_plan_dialog_type2_list, null)
                                                                val tvName = view.findViewById<TextView>(R.id.dialog_type_name)

                                                                tvName.text = item.optDictionaryName
                                                                it.addView(view)
                                                            }
                                                        }
                                                    }
                                                }

                                                .clicked(R.id.item_plan) {
                                                    if (data.isClickable) {
                                                        data.isChecked = !data.isChecked
                                                        (this.adapter as SlimAdapter).notifyDataSetChanged()
                                                    }
                                                }
                                    }
                                    .attachTo(this)
                        }
                        (dialogList.adapter as SlimAdapter).updateData(itemLbs)

                        dialogTitle.text = intent.getStringExtra("title")
                        dialogClose.setOnClickListener { dismiss() }
                        dialogOk.setOnClickListener { dismiss() }

                        return view
                    }

                }
                dialog.show()
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.prospectus_detils)
                .tag(this@PlanMakeActivity)
                .params("prospectusId", intent.getStringExtra("prospectusId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                        listKind.addItems(response.body().`object`.lks)

                        val items = ArrayList<InsuranceModel>()
                        items.addItems(response.body().`object`.lbs)
                        listKind.forEach { item->
                            if (items.any { it.insuranceKindId == item.insuranceKindId })
                                mapKind[item.insuranceKindId] = items
                        }

                        if (items.isNotEmpty()) {
                            val data = items.first { it.type == "1" }
                            mStartAge = data.startAge.toInt()
                            mMedianAge = data.medianAge.toInt()
                            mEndAge = data.endAge.toInt()

                            mCheckAge = mMedianAge
                            planed_age.text = "${mMedianAge}岁"
                            val year = Calendar.getInstance().get(Calendar.YEAR)
                            mPlanedYear = year - mCheckAge
                            planed_birth.text = "生日"

                            if (data.inshow == "1") {
                                plan_title_ll.gone()
                                plan_append.visible()
                                val itemLds = ArrayList<CommonData>()
                                val itemLis = ArrayList<CommonData>()
                                val itemLrs = ArrayList<CommonData>()
                                var itemRange = "——"

                                itemLds.addItems(data.lds)
                                itemLis.addItems(data.lis)
                                itemLrs.addItems(data.lrs)

                                if (itemLds.any { it.optDictionaryName == "交费期间" }) {
                                    val optId = itemLds.filter { it.optDictionaryName == "交费期间" }[0].insuranceOptDictionaryId
                                    itemRange = itemLis.filter { it.insuranceOptDictionaryId == optId && it.checkItem == "1" }[0].itemName
                                }

                                val proportionSum = when (itemLds.filter { it.type == "0" }.size) {
                                    1 -> {
                                        val itemId = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[0].insuranceOptDictionaryId
                                                        && it.checkItem == "1"
                                        }[0].insuranceItemId

                                        itemLrs.filter { it.item1 == itemId }[0].proportion
                                    }
                                    2 -> {
                                        val itemId1 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[0].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId2 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[1].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId

                                        itemLrs.filter { it.item1 == itemId1 && it.item2 == itemId2 }[0].proportion
                                    }
                                    3 -> {
                                        val itemId1 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[0].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId2 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[1].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId3 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[2].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId

                                        itemLrs.filter {
                                            it.item1 == itemId1
                                                    && it.item2 == itemId2
                                                    && it.item3 == itemId3
                                        }[0].proportion
                                    }
                                    4 -> {
                                        val itemId1 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[0].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId2 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[1].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId3 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[2].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId
                                        val itemId4 = itemLis.filter {
                                            it.insuranceOptDictionaryId == itemLds[3].insuranceOptDictionaryId
                                                    && it.checkItem == "1"
                                        }[0].insuranceItemId

                                        itemLrs.filter {
                                            it.item1 == itemId1
                                                    && it.item2 == itemId2
                                                    && it.item3 == itemId3
                                                    && it.item4 == itemId4
                                        }[0].proportion
                                    }
                                    else -> "0"
                                }

                                listChecked.add(InsuranceData().apply {
                                    insuranceKindId = data.insuranceKindId
                                    insuranceKindName = data.insuranceKindName
                                    insuredAmount = data.dfInsuredAmount.toDouble()
                                    proportion = proportionSum.toDouble()
                                    insuredFee = insuredAmount * proportion
                                    insuredRange = itemRange
                                    isDelete = false
                                    listTable.add(TableData("险种", "保额", "保费", "交费期间"))
                                    listTable.add(TableData(
                                            insuranceKindName,
                                            DecimalFormat("0.00").format(insuredAmount),
                                            DecimalFormat("0.00").format(insuredFee),
                                            insuredRange))
                                })

                                (plan_list.adapter as SlimAdapter).updateData(listChecked)
                            }
                        }
                    }

                })
    }
}
