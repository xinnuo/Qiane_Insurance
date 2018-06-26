package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
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
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.sdk25.listeners.onCheckedChange
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.support.v7.widget.SimpleItemAnimator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 1, 2 -> {

                }
            }
        }
    }

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

            handler.sendEmptyMessage(0)
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
                                        "首年保费：${DecimalFormat("0.00").format(data.insuredTotalFee)}")
                                .visibility(R.id.item_plan_del, if (data.isDelete) View.VISIBLE else View.GONE)

                                .with<RecyclerView>(R.id.item_plan_table) {
                                    it.apply {
                                        isNestedScrollingEnabled = false
                                        layoutManager = FullyLinearLayoutManager(baseContext)
                                        adapter = SlimAdapter.create()
                                                .register<TableData>(R.layout.item_plan_table_list) { item, injector ->
                                                    val isLast = data.insuredTable.indexOf(item) == data.insuredTable.size - 1

                                                    injector.text(R.id.item_plan_column1, item.name)
                                                            .text(R.id.item_plan_column2, item.amount)
                                                            .text(R.id.item_plan_column3, item.fee)
                                                            .text(R.id.item_plan_column4, item.range)
                                                            .visibility(R.id.item_plan_divider, if (isLast) View.GONE else View.VISIBLE)
                                                }
                                                .attachTo(this)

                                        (it.adapter as SlimAdapter).updateData(data.insuredTable)
                                    }
                                }
                    }
                    .attachTo(this)

            (adapter as SlimAdapter).updateData(listChecked)
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

                    handler.sendEmptyMessage(1)
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

                    handler.sendEmptyMessage(2)
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
                if (mapKind.isEmpty()) {
                    showToast("获取数据失败")
                    return
                }

                @Suppress("DEPRECATION")
                val dialog = object : BottomDialog(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onCreateView(): View {
                        val view = View.inflate(mContext, R.layout.dialog_plan_host, null)
                        val dialogTitle = view.findViewById<TextView>(R.id.dialog_plan_title)
                        val dialogClose = view.findViewById<ImageView>(R.id.dialog_plan_close)
                        val dialogList = view.findViewById<RecyclerView>(R.id.dialog_plan_list)
                        val dialogOk = view.findViewById<Button>(R.id.dialog_plan_ok)

                        val items = ArrayList<Any>()
                        val itemLbs = ArrayList<InsuranceModel>()

                        itemLbs.addItems(mapKind[listKind[0].insuranceKindId])
                        items.addAll(getInsuranceList(itemLbs))

                        dialogList.apply {
                            isNestedScrollingEnabled = false
                            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false //解决notifyItemChanged闪烁
                            layoutManager = FullyLinearLayoutManager(baseContext)
                            adapter = SlimAdapter.create()
                                    .register<InsuranceModel>(R.layout.item_plan_dialog_list) { data, injector ->
                                        val isFirst = items.indexOf(data) == 0
                                        val isLast = items.indexOf(data) == items.size - 1

                                        injector.text(R.id.item_plan_check, data.insuranceKindName)
                                                .textColor(R.id.item_plan_check, resources.getColor(if (data.isGray) R.color.light else R.color.black))
                                                .checked(R.id.item_plan_check, data.isChecked)
                                                .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                                                .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)
                                                .visibility(R.id.item_plan_divider3, if (!isFirst) View.GONE else View.VISIBLE)

                                                .clicked(R.id.item_plan) {
                                                    if (data.isClickable) {
                                                        data.isChecked = !data.isChecked
                                                        (this.adapter as SlimAdapter).notifyDataSetChanged()
                                                    }
                                                }
                                    }
                                    .register<CommonData>(R.layout.item_plan_dialog_type1_list) { data, injector ->
                                        val isLast = items.indexOf(data) == items.size - 1

                                        injector.text(R.id.dialog_type_name, data.optDictionaryName)
                                                .visibility(R.id.dialog_type_divider, if (!isLast) View.GONE else View.VISIBLE)

                                                .with<TextView>(R.id.dialog_type_age) {
                                                    it.hint = "请选择"
                                                    it.text = data.checkName
                                                }

                                                .clicked(R.id.dialog_type_age_ll) {
                                                    val options = ArrayList<CommonData>()
                                                    val itemStr = ArrayList<String>()

                                                    options.addItems(itemLbs[data.insuredParentPosition].lis?.filter {
                                                        it.insuranceOptDictionaryId == data.insuranceOptDictionaryId
                                                    }).forEach { itemStr.add(it.itemName) }

                                                    DialogHelper.showItemDialog(
                                                            baseContext,
                                                            "选择${data.optDictionaryName}",
                                                            itemStr) { position, name ->

                                                        data.checkName = name
                                                        data.checkItemId = options[position].insuranceItemId
                                                        calculateFee(items)

                                                        (adapter as SlimAdapter).notifyDataSetChanged()
                                                    }
                                                }
                                    }
                                    .register<InsuranceData>(R.layout.item_plan_dialog_type2_list) { data, injector ->
                                        val isLast = items.indexOf(data) == items.size - 1

                                        injector.text(R.id.dialog_type_name, data.optDictionaryName)
                                                .visibility(R.id.dialog_type_divider, if (!isLast) View.GONE else View.VISIBLE)

                                                .with<EditText>(R.id.dialog_type_num) {
                                                    if (it.tag != null && it.tag is TextWatcher) {
                                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                                    }

                                                    it.setText(data.checkName)
                                                    it.setSelection(it.text.length)
                                                    it.hint = "请输入"

                                                    val textWatcher = object : TextWatcher {
                                                        override fun afterTextChanged(s: Editable) {
                                                            data.checkName = s.toString()
                                                        }

                                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                                    }

                                                    it.addTextChangedListener(textWatcher)
                                                    it.tag = textWatcher
                                                }

                                                .clicked(R.id.dialog_type_num_clear) {
                                                    data.checkName = ""
                                                    (adapter as SlimAdapter).notifyItemChanged(items.indexOf(data))
                                                }
                                    }
                                    .attachTo(this)
                        }
                        (dialogList.adapter as SlimAdapter).updateData(items)

                        dialogTitle.text = intent.getStringExtra("title")
                        dialogClose.setOnClickListener { dismiss() }
                        dialogOk.setOnClickListener {
                            dismiss()

                            updateInsuranceList(items)
                        }

                        return view
                    }

                }
                dialog.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateList() {
        var totalFee = 0.0
        listChecked.forEach { totalFee += it.insuredTotalFee }
        plan_fee.text = "￥${DecimalFormat("0.00").format(totalFee)}"
        (plan_list.adapter as SlimAdapter).notifyDataSetChanged()
    }

    private fun getInsuranceList(itemLbs: ArrayList<InsuranceModel>): ArrayList<Any> {
        val items = ArrayList<Any>()
        itemLbs.forEachWithIndex { index, it ->
            it.isClickable = it.ichange == "1"
            it.isExpand = it.inshow == "1"
            it.isChecked = it.pitchOn == "1"
            it.isGray = !it.isChecked && !it.isClickable
            it.insuredParentPosition = index

            calculateBaseProportion(it) //根据所选年龄计算基准费率

            items.add(it)
            if (it.isChecked) {
                val itemType = ArrayList<CommonData>()
                val kindItemIds = it.pinsuranceKindItemIds

                itemType.addItems(it.lds)
                itemType.forEachWithIndex continuing@{ inner, item ->
                    if (kindItemIds.contains(item.insuranceOptDictionaryId)) return@continuing

                    val itemLis = ArrayList<CommonData>()
                    itemLis.addItems(it.lis)

                    if (item.type == "0") {
                        if (itemLis.any {
                                    it.insuranceOptDictionaryId == item.insuranceOptDictionaryId
                                            && it.checkItem == "1"
                                }) {
                            val data = itemLis.filter {
                                it.checkItem == "1"
                                        && it.insuranceOptDictionaryId == item.insuranceOptDictionaryId
                            }[0]

                            item.checkItemId = data.insuranceItemId
                            item.checkName = data.itemName
                        }
                        item.insuredParentPosition = index
                        items.add(item)
                    } else {
                        items.add(InsuranceData().apply {
                            insuredPosition = inner
                            insuredParentPosition = index
                            insuranceOptDictionaryId = item.insuranceOptDictionaryId
                            optDictionaryName = item.optDictionaryName
                            type = item.type

                            if (item.optDictionaryName == "保额") checkName = it.dfInsuredAmount
                        })
                    }
                }
            }
        }

        calculateFee(items)

        return items
    }

    private fun updateInsuranceList(items: ArrayList<Any>) {
        Flowable.just<ArrayList<Any>>(items)
                .map { inner ->
                    val mainKind = inner.first { it is InsuranceModel && it.type == "1" } as InsuranceModel
                    val kindId = mainKind.insuranceKindId
                    val kindName = mainKind.insuranceKindName
                    val item = InsuranceData().apply {
                        insuranceKindId = kindId
                        insuranceKindName = kindName
                        isDelete = kindId != listKind[0].insuranceKindId
                        insuredTable.add(TableData("险种", "保额", "保费", "交费期间"))
                    }

                    var kindFee = 0.0

                    inner.filter { it is InsuranceModel }.forEachWithIndex { index, data ->
                        data as InsuranceModel
                        val itemName = data.insuranceKindName
                        var itemAmount = "——"
                        var itemFee = "——"
                        var itemRange = "——"
                        var itemPeriod = "——"

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "保额"
                                            && it.insuredParentPosition == index
                                }) {
                            itemAmount = (inner.filter {
                                it is InsuranceData
                                        && it.optDictionaryName == "保额"
                                        && it.insuredParentPosition == index
                            }[0] as InsuranceData).checkName
                        }

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "保费"
                                            && it.insuredParentPosition == index
                                }) {
                            itemFee = (inner.filter {
                                it is InsuranceData
                                        && it.optDictionaryName == "保费"
                                        && it.insuredParentPosition == index
                            }[0] as InsuranceData).checkName

                            kindFee += itemFee.toDouble()
                        }

                        if (inner.any {
                                    it is CommonData
                                            && it.optDictionaryName == "交费期间"
                                            && it.insuredParentPosition == index
                                }) {
                            itemRange = (inner.filter {
                                it is CommonData
                                        && it.optDictionaryName == "交费期间"
                                        && it.insuredParentPosition == index
                            }[0] as CommonData).checkName
                        } else {
                            if (data.pinsuranceKindItemIds.isNotEmpty()) {
                                val itemMainLds = ArrayList<CommonData>()
                                itemMainLds.addItems(mainKind.lds)
                                if (itemMainLds.any { it.optDictionaryName == "交费期间" }) {
                                    itemRange = itemMainLds.first { it.optDictionaryName == "交费期间" }.checkName
                                }
                            }
                        }

                        if (inner.any {
                                    it is CommonData
                                            && it.optDictionaryName == "保险期间"
                                            && it.insuredParentPosition == index
                                }) {
                            itemPeriod = (inner.filter {
                                it is CommonData
                                        && it.optDictionaryName == "保险期间"
                                        && it.insuredParentPosition == index
                            }[0] as CommonData).checkName
                        } else {
                            if (data.pinsuranceKindItemIds.isNotEmpty()) {
                                val itemMainLds = ArrayList<CommonData>()
                                itemMainLds.addItems(mainKind.lds)
                                if (itemMainLds.any { it.optDictionaryName == "保险期间" }) {
                                    itemPeriod = itemMainLds.first { it.optDictionaryName == "保险期间" }.checkName
                                }
                            }
                        }

                        item.insuredTable.add(TableData(itemName, itemAmount, itemFee, itemRange))
                        item.insuredItem.add(InsuranceItemData(data, itemAmount, itemFee, itemPeriod, itemRange)
                        )
                    }

                    item.insuredTotalFee = kindFee

                    if (listChecked.any { it.insuranceKindId == item.insuranceKindId }) {
                        val pos = listChecked.indexOfFirst { it.insuranceKindId == item.insuranceKindId }
                        listChecked[pos] = item
                    } else listChecked.add(item)
                    return@map listChecked
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateList() }
    }

    /**
     * 根据所选年龄计算基准费率
     */
    private fun calculateBaseProportion(item: InsuranceModel) {
        val medianAge = item.medianAge.toInt()
        when {
            mCheckAge < medianAge -> {
                val addAge = item.addAge2.toInt()
                val rule = item.gtMedianAgeRule.toDouble()
                item.baseProportion = ((medianAge - mCheckAge) / addAge) * rule
            }
            mCheckAge < medianAge -> {
                val addAge = item.addAge.toInt()
                val rule = item.tlMedianAgeRule.toDouble()
                item.baseProportion = ((mCheckAge - medianAge) / addAge) * rule
            }
        }
        if (mPlanedSex == 0) item.baseProportion += item.femaleRule.toDouble()
    }

    /**
     * 计算保费
     */
    private fun calculateFee(items: ArrayList<Any>) {
        if (items.isEmpty()) return
        // 主险
        val dataMain = items.filter { it is InsuranceModel && it.type == "1" }[0] as InsuranceModel

        items.filter { it is InsuranceModel }.forEachWithIndex { index, data ->
            // 当前所选险种
            data as InsuranceModel
            val baseProportion = data.baseProportion
            var amount = data.dfInsuredAmount

            val itemLrs = ArrayList<CommonData>()
            itemLrs.addItems((items.filter {
                it is InsuranceModel && it.insuredParentPosition == index
            }[0] as InsuranceModel).lrs)

            val itemLds = ArrayList<CommonData>()
            if (data.pinsuranceKindItemIds.isNotEmpty()) {
                val itemIds = data.insuranceKindItemIds
                val itemMainLds = ArrayList<CommonData>()
                itemMainLds.addItems(dataMain.lds) //选项列表里已包含初始值
                itemMainLds.filter { it.type == "0" }.forEach {
                    if (itemIds.contains(it.insuranceOptDictionaryId)) itemLds.add(it)
                }
            }
            @Suppress("UNCHECKED_CAST")
            itemLds.addAll(items.filter { it is CommonData && it.insuredParentPosition == index } as ArrayList<CommonData>)

            val proportion = when (itemLds.size) {
                1 -> {
                    val itemId = itemLds[0].checkItemId
                    itemLrs.filter { it.item1 == itemId }[0].proportion
                }
                2 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    itemLrs.filter { it.item1 == itemId1 && it.item2 == itemId2 }[0].proportion
                }
                3 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    val itemId3 = itemLds[2].checkItemId

                    itemLrs.filter {
                        it.item1 == itemId1
                                && it.item2 == itemId2
                                && it.item3 == itemId3
                    }[0].proportion
                }
                4 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    val itemId3 = itemLds[2].checkItemId
                    val itemId4 = itemLds[3].checkItemId

                    itemLrs.filter {
                        it.item1 == itemId1
                                && it.item2 == itemId2
                                && it.item3 == itemId3
                                && it.item4 == itemId4
                    }[0].proportion
                }
                else -> "0"
            }

            data.proportion = proportion.toDouble()
            val proportionSum = baseProportion + proportion.toDouble()

            if (items.any {
                        it is InsuranceData
                                && it.optDictionaryName == "保额"
                                && it.insuredParentPosition == index
                    }) {
                amount = (items.filter {
                    it is InsuranceData
                            && it.optDictionaryName == "保额"
                            && it.insuredParentPosition == index
                }[0] as InsuranceData).checkName
            }

            if (items.any {
                        it is InsuranceData
                                && it.optDictionaryName == "保费"
                                && it.insuredParentPosition == index
                    }) {
                (items.filter {
                    it is InsuranceData
                            && it.optDictionaryName == "保费"
                            && it.insuredParentPosition == index
                }[0] as InsuranceData).checkName = DecimalFormat("0.00").format(amount.toDouble() * proportionSum)
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
                        listKind.forEach { item ->
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

                            val itemShow = ArrayList<Any>()
                            val itemLbs = ArrayList<InsuranceModel>()

                            itemLbs.addItems(items.filter { data.inshow == "1" })
                            itemShow.addAll(getInsuranceList(itemLbs))

                            if (itemShow.isNotEmpty()) updateInsuranceList(itemShow)
                        }
                    }

                })
    }
}
