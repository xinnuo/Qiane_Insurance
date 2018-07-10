package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyLinearLayoutManager
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_plan_make.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.sdk25.listeners.onCheckedChange
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.sdk25.listeners.onTouch
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlanMakeActivity : BaseActivity() {

    private val listKind = ArrayList<CommonData>()
    private val listChecked = ArrayList<InsuranceData>()
    private val mapKind = HashMap<String, List<InsuranceModel>>()
    private val mapChecked = HashMap<String, ArrayList<Any>>()
    private var mProductId = ""
    //被保人
    private var mPlanedSex = 1
    private var mPlanedYear: Int = 0
    private var mPlanedMonth: Int = 0
    private var mPlanedDay: Int = 0
    private var mStartAge = 0
    private var mMedianAge = 0
    private var mEndAge = 0
    private var mCheckAge = 0
    //投保人
    private var mPlanSex = 1
    private var mPlanYear: Int = 0
    private var mPlanMonth: Int = 0
    private var mPlanDay: Int = 0
    private var mStartAge1 = 0
    private var mMedianAge1 = 0
    private var mEndAge1 = 0
    private var mCheckAge1 = 0
    //投保人配偶
    private var mPlaneSex = 0
    private var mPlaneYear: Int = 0
    private var mPlaneMonth: Int = 0
    private var mPlaneDay: Int = 0
    private var mStartAge2 = 0
    private var mMedianAge2 = 0
    private var mEndAge2 = 0
    private var mCheckAge2 = 0

    private var mPlanerSex = 1
    private var mFocusPosition = -1

    @SuppressLint("HandlerLeak")
    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 1, 2 -> { //性别，年龄，生日
                    if (listChecked.isEmpty()) return

                    updateCheckedList()
                    updateList()
                }
                3 -> { //险种信息
                    val kindId = msg.obj as String
                    getInsuranceData(kindId)
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

        mPlanYear = mPlanedYear
        mPlanMonth = mPlanedMonth
        mPlanDay = mPlanedDay

        mPlaneYear = mPlanedYear
        mPlaneMonth = mPlanedMonth
        mPlaneDay = mPlanedDay

        plan_select.onCheckedChange { _, isChecked ->
            if (isChecked) plan_expand.expand()
            else {
                plan_expand.collapse()
                plane_select.isChecked = false
            }
        }

        plane_select.onCheckedChange { _, isChecked ->
            if (isChecked) plane_expand.expand()
            else plane_expand.collapse()
        }

        plane_select.onTouch { _, _ -> return@onTouch !plan_select.isChecked }

        planed_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.planed_check1 -> mPlanedSex = 1
                R.id.planed_check2 -> mPlanedSex = 0
            }

            mHandler.sendEmptyMessage(0)
        }

        plan_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.plan_check1 -> mPlanSex = 1
                R.id.plan_check2 -> mPlanSex = 0
            }

            mHandler.sendEmptyMessage(0)

        }

        plane_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.plane_check1 -> mPlaneSex = 1
                R.id.plane_check2 -> mPlaneSex = 0
            }

            mHandler.sendEmptyMessage(0)

        }

        planer_check.onCheckedChange { _, checkedId ->
            when (checkedId) {
                R.id.planer_check1 -> mPlanerSex = 1
                R.id.planer_check2 -> mPlanerSex = 0
            }
        }

        planed_check.check(R.id.planed_check1)
        plan_check.check(R.id.plan_check1)
        plane_check.check(R.id.plane_check2)
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

                                .clicked(R.id.item_plan_modify) {
                                    Flowable.just<String>(data.insuranceKindId)
                                            .map { id ->
                                                val items = mapChecked[id]!!
                                                val indexPosition = items.indexOfFirst { it is CommonData }
                                                calculateFee(indexPosition, 0, items)
                                                return@map items
                                            }
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe { showInsuranceDialog(data.insuranceKindId, it) }
                                }

                                .clicked(R.id.item_plan_del) {
                                    val index = listChecked.indexOf(data)
                                    listChecked.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
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
            R.id.plan_data -> startActivity<PlanLookActivity>(
                    "productinId" to mProductId,
                    "type" to "产品详情",
                    "outHref" to "")
            R.id.plan_make -> {
                if (listChecked.isEmpty()) {
                    showToast("请选择险种")
                    return
                }

                val obj = JSONObject()
                obj.put("prospectusId", intent.getStringExtra("prospectusId"))
                obj.put("insuranceSum", plan_fee.text.toString().replace("￥", ""))
                obj.put("coverAge", mCheckAge)
                obj.put("coverBirthday", if (planed_birth.text.toString() == "生日") "" else planed_birth.text.toString())
                obj.put("coverSex", mPlanedSex)
                obj.put("throwAge", mCheckAge1)
                obj.put("throwBirthday", if (plan_birth.text.toString() == "生日") "" else plan_birth.text.toString())
                obj.put("throwSex", mPlanSex)
                obj.put("throwWifeAge", mCheckAge2)
                obj.put("throwWifeBirthday", if (plane_birth.text.toString() == "生日") "" else plane_birth.text.toString())
                obj.put("throwWifeSex", mPlaneSex)
                obj.put("consigneeName", planr_name.text.trim().toString())
                obj.put("consigneeSex", mPlanerSex)
                val objArr = JSONArray()
                listChecked.forEach { data ->
                    val items = data.insuredItem
                    items.forEach {
                        val objItem = JSONObject()
                        objItem.put("insuranceKindId", it.insuranceItem.insuranceKindId)
                        objItem.put("insuredAmount", it.insuredAmount)
                        objItem.put("premium", it.insuredFee)
                        objItem.put("insuredPeriod", it.insuredPeriod)
                        objItem.put("paymentPeriod", it.insuredRange)
                        objArr.put(objItem)
                    }
                }
                obj.put("lrs", objArr)

                OkGo.post<String>(BaseHttp.add_app_prospectus)
                        .tag(this@PlanMakeActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("conent", obj.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                val json = JSONObject(response.body()).optJSONObject("object")
                                        ?: JSONObject()
                                val prospectusTitle = json.optString("prospectusTitle")
                                val prospectusId = json.optString("prospectusId")
                                startActivity<PlanLookActivity>(
                                        "title" to prospectusTitle,
                                        "prospectusId" to prospectusId,
                                        "type" to "计划书")
                                ActivityStack.screenManager.popActivities(this@PlanMakeActivity::class.java)
                            }

                        })
            }
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

                    mHandler.sendEmptyMessage(1)
                }
            }
            R.id.plan_age_ll -> {
                val items = ArrayList<String>()
                (mStartAge1 until mEndAge1 + 1).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        mCheckAge1 - mStartAge1,
                        items) { position, name ->
                    mCheckAge1 = mStartAge1 + position
                    plan_age.text = name

                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    mPlanYear = year - mStartAge1 - position
                    plan_birth.text = "生日"

                    mHandler.sendEmptyMessage(1)
                }
            }
            R.id.plane_age_ll -> {
                val items = ArrayList<String>()
                (mStartAge2 until mEndAge2 + 1).forEach { items.add("${it}岁") }
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        mCheckAge2 - mStartAge2,
                        items) { position, name ->
                    mCheckAge2 = mStartAge2 + position
                    plane_age.text = name

                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    mPlaneYear = year - mStartAge2 - position
                    plane_birth.text = "生日"

                    mHandler.sendEmptyMessage(1)
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

                    mHandler.sendEmptyMessage(2)
                }
            }
            R.id.plan_birth -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - mEndAge1,
                        year - mStartAge1,
                        "选择生日",
                        mPlanYear,
                        mPlanMonth,
                        mPlanDay,
                        false) { y, m, d, _, _, date ->
                    mPlanYear = y
                    mPlanMonth = m - 1
                    mPlanDay = d

                    mCheckAge1 = year - y
                    plan_age.text = "${mCheckAge1}岁"
                    plan_birth.text = date

                    mHandler.sendEmptyMessage(2)
                }
            }
            R.id.plane_birth -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - mEndAge2,
                        year - mStartAge2,
                        "选择生日",
                        mPlaneYear,
                        mPlaneMonth,
                        mPlaneDay,
                        false) { y, m, d, _, _, date ->
                    mPlaneYear = y
                    mPlaneMonth = m - 1
                    mPlaneDay = d

                    mCheckAge2 = year - y
                    plane_age.text = "${mCheckAge2}岁"
                    plane_birth.text = date

                    mHandler.sendEmptyMessage(2)
                }
            }
            R.id.plan_host -> {
                if (mapKind.isEmpty()) {
                    showToast("数据加载失败")
                    return
                }

                showOptionDialog()
            }
        }
    }

    private fun showOptionDialog() {
        val items = ArrayList<CommonData>()
        listKind.forEach { item -> if (!listChecked.any { it.insuranceKindId == item.insuranceKindId }) items.add(item) }

        val dialog = object : BottomDialog(baseContext) {

            override fun onCreateView(): View {
                val view = View.inflate(mContext, R.layout.dialog_plan_option, null)
                val dialogClose = view.findViewById<ImageView>(R.id.dialog_plan_close)
                val dialogList = view.findViewById<RecyclerView>(R.id.dialog_plan_list)

                dialogList.apply {
                    load_Linear(baseContext)
                    adapter = SlimAdapter.create()
                            .register<CommonData>(R.layout.item_plan_option_list) { data, injector ->
                                val isLast = items.indexOf(data) == items.size - 1

                                injector.text(R.id.item_option_title, data.insuranceKindName)
                                        .visibility(R.id.item_option_divider1, if (isLast) View.GONE else View.VISIBLE)
                                        .visibility(R.id.item_option_divider2, if (!isLast) View.GONE else View.VISIBLE)

                                        .clicked(R.id.item_option) {
                                            dismiss()
                                            mHandler.sendMessageDelayed(Message().apply {
                                                what = 3
                                                obj = data.insuranceKindId
                                            }, 300)
                                        }
                            }
                            .attachTo(this)
                    (adapter as SlimAdapter).updateData(items)
                }

                dialogClose.onClick { dismiss() }

                return view
            }
        }
        dialog.show()
    }

    private fun showInsuranceDialog(kindId: String, items: ArrayList<Any>) {
        @Suppress("DEPRECATION")
        val dialog = object : BottomDialog(baseContext) {

            @SuppressLint("SetTextI18n")
            override fun onCreateView(): View {
                val view = View.inflate(mContext, R.layout.dialog_plan_host, null)
                val dialogTitle = view.findViewById<TextView>(R.id.dialog_plan_title)
                val dialogClose = view.findViewById<ImageView>(R.id.dialog_plan_close)
                val dialogList = view.findViewById<RecyclerView>(R.id.dialog_plan_list)
                val dialogOk = view.findViewById<Button>(R.id.dialog_plan_ok)

                dialogList.apply {
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false //解决notifyItemChanged闪烁
                    layoutManager = LinearLayoutManager(baseContext)
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

                                                Observable.create<Int> {
                                                    val index = items.indexOf(data)
                                                    updateInsuranceList(items.indexOf(data), items)
                                                    it.onNext(index)
                                                }
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
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

                                            val item = items.first {
                                                it is InsuranceModel
                                                        && it.insuredParentPosition == data.insuredParentPosition
                                            } as InsuranceModel
                                            options.addItems(item.lis?.filter {
                                                it.insuranceOptDictionaryId == data.insuranceOptDictionaryId
                                            }).forEach { itemStr.add(it.itemName) }

                                            DialogHelper.showItemDialog(
                                                    baseContext,
                                                    "选择${data.optDictionaryName}",
                                                    itemStr) { position, name ->

                                                data.checkName = name
                                                data.checkItemId = options[position].insuranceItemId

                                                calculateProportion(items)
                                                calculateFee(items.indexOf(data), data.insuredParentPosition, items)
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
                                            if (mFocusPosition == items.indexOf(data)) it.requestFocus()

                                            val textWatcher = object : TextWatcher {
                                                override fun afterTextChanged(s: Editable) {
                                                    data.checkName = s.toString()
                                                    mFocusPosition = items.indexOf(data)

                                                    Observable.create<Int> {
                                                        calculateFee(mFocusPosition, data.insuredParentPosition, items)
                                                        it.onNext(mFocusPosition)
                                                    }
                                                            //.debounce(600, TimeUnit.MILLISECONDS) //防抖动
                                                            .subscribeOn(Schedulers.newThread())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                                }

                                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                            }

                                            it.addTextChangedListener(textWatcher)
                                            it.tag = textWatcher
                                        }

                                        .clicked(R.id.dialog_type_num_clear) {
                                            Observable.create<Int> {
                                                val index = items.indexOf(data)
                                                data.checkName = ""
                                                calculateFee(index, data.insuredParentPosition, items)
                                                it.onNext(index)
                                            }
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                        }
                            }
                            .attachTo(this)
                    (adapter as SlimAdapter).updateData(items)
                }

                dialogTitle.text = intent.getStringExtra("title")
                dialogClose.onClick { dismiss() }
                dialogOk.onClick {

                    items.forEach { item ->
                        when (item) {
                            is CommonData -> if (item.checkName.isEmpty()) {
                                showToast("请选择${item.optDictionaryName}")
                                return@onClick
                            }
                            is InsuranceData -> if (item.checkName.isEmpty()) {
                                showToast("请输入${item.optDictionaryName}")
                                return@onClick
                            }
                        }
                    }

                    dismiss()

                    getCheckedList(items)

                    mapChecked[kindId] = items
                }

                return view
            }

        }
        dialog.show()
    }

    /**
     * 获取已选险种列表
     */
    private fun getCheckedList(items: ArrayList<Any>) {
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
                        if (!data.isChecked) return@forEachWithIndex

                        val itemName = data.insuranceKindName
                        var itemAmount = "-"
                        var itemFee = "-"
                        var itemRange = "-"
                        var itemPeriod = "-"

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "保额"
                                            && it.insuredParentPosition == index
                                }) {
                            itemAmount = (inner.first {
                                it is InsuranceData
                                        && it.optDictionaryName == "保额"
                                        && it.insuredParentPosition == index
                            } as InsuranceData).checkName
                        }

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "保费"
                                            && it.insuredParentPosition == index
                                }) {
                            itemFee = (inner.first {
                                it is InsuranceData
                                        && it.optDictionaryName == "保费"
                                        && it.insuredParentPosition == index
                            } as InsuranceData).checkName

                            kindFee += itemFee.toDouble()
                        }

                        if (inner.any {
                                    it is CommonData
                                            && it.optDictionaryName == "交费期间"
                                            && it.insuredParentPosition == index
                                }) {
                            itemRange = (inner.first {
                                it is CommonData
                                        && it.optDictionaryName == "交费期间"
                                        && it.insuredParentPosition == index
                            } as CommonData).checkName
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
                            itemPeriod = (inner.first {
                                it is CommonData
                                        && it.optDictionaryName == "保险期间"
                                        && it.insuredParentPosition == index
                            } as CommonData).checkName
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
                        item.insuredItem.add(InsuranceItemData(data, itemAmount, itemFee, itemPeriod, itemRange))
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
     * 更新已选险种列表
     */
    private fun updateCheckedList() {
        listChecked.forEach { item ->
            val items = item.insuredItem
            val itemTable = item.insuredTable
            var totalFee = 0.0

            itemTable.clear()
            itemTable.add(TableData("险种", "保额", "保费", "交费期间"))

            items.forEach {
                calculateBaseProportion(it.insuranceItem)

                val fee = it.insuredAmount.toDouble() * (it.insuranceItem.baseProportion + it.insuranceItem.proportion)
                it.insuredFee = DecimalFormat("0.00").format(fee)

                totalFee += fee
                itemTable.add(TableData(it.insuranceItem.insuranceKindName, it.insuredAmount, it.insuredFee, it.insuredRange))
            }

            item.insuredTotalFee = totalFee
        }
    }

    /**
     * 获取险种列表
     */
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
                                    it.checkItem == "1"
                                            && it.insuranceOptDictionaryId == item.insuranceOptDictionaryId
                                }) {
                            val data = itemLis.first {
                                it.checkItem == "1"
                                        && it.insuranceOptDictionaryId == item.insuranceOptDictionaryId
                            }

                            item.checkItemId = data.insuranceItemId
                            item.checkName = data.itemName
                        }
                        item.insuredParentPosition = index
                        items.add(item)
                    } else {
                        items.add(InsuranceData().apply {
                            insuredPosition = inner + index + 1
                            insuredParentPosition = index
                            insuranceOptDictionaryId = item.insuranceOptDictionaryId
                            optDictionaryName = item.optDictionaryName
                            type = item.type

                            if (item.optDictionaryName == "保额") checkName = it.dfInsuredAmount
                            if (item.optDictionaryName == "份数") checkName = it.dfcopies
                        })
                    }
                }
            }
        }

        calculateProportion(items)
        val indexPosition = items.indexOfFirst { it is CommonData }
        calculateFee(indexPosition, 0, items)

        return items
    }

    /**
     * 更新险种列表
     */
    private fun updateInsuranceList(currentPos: Int, items: ArrayList<Any>) {
        val item = items[currentPos] as InsuranceModel
        if (item.isChecked) {
            val itemLbs = ArrayList<Any>()
            val itemType = ArrayList<CommonData>()
            val kindItemIds = item.pinsuranceKindItemIds

            itemType.addItems(item.lds)
            itemType.forEachWithIndex continuing@{ index, inner ->
                if (kindItemIds.contains(inner.insuranceOptDictionaryId)) return@continuing

                val itemLis = ArrayList<CommonData>()
                itemLis.addItems(item.lis)

                if (inner.type == "0") {
                    if (itemLis.any {
                                it.checkItem == "1"
                                        && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                            }) {
                        val data = itemLis.first {
                            it.checkItem == "1"
                                    && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                        }

                        inner.checkItemId = data.insuranceItemId
                        inner.checkName = data.itemName
                    }
                    inner.insuredParentPosition = item.insuredParentPosition
                    itemLbs.add(inner)
                } else {
                    itemLbs.add(InsuranceData().apply {
                        insuredPosition = index + currentPos + 1
                        insuredParentPosition = item.insuredParentPosition
                        insuranceOptDictionaryId = inner.insuranceOptDictionaryId
                        optDictionaryName = inner.optDictionaryName
                        type = inner.type

                        if (inner.optDictionaryName == "保额") checkName = item.dfInsuredAmount
                        if (inner.optDictionaryName == "份数") checkName = item.dfcopies
                    })
                }
            }

            items.addAll(currentPos + 1, itemLbs)

            calculateProportion(items)
            val indexPosition = items.indexOfFirst { it is CommonData }
            calculateFee(indexPosition, 0, items)
        } else {
            items.removeAll {
                (it is CommonData && it.insuredParentPosition == item.insuredParentPosition)
                        || (it is InsuranceData && it.insuredParentPosition == item.insuredParentPosition)
            }
        }
    }

    /**
     * 根据所选年龄计算基准费率
     */
    private fun calculateBaseProportion(item: InsuranceModel) {
        val checkAge = when (item.type) {
            "1", "2" -> mCheckAge
            "3" -> mCheckAge1
            "4" -> mCheckAge2
            else -> 0
        }
        val medianAge = item.medianAge.toInt()
        when {
            checkAge < medianAge -> {
                val addAge = item.addAge2.toInt()
                val rule = item.gtMedianAgeRule.toDouble()
                item.baseProportion = ((medianAge - checkAge) / addAge) * rule
            }
            checkAge > medianAge -> {
                val addAge = item.addAge.toInt()
                val rule = item.tlMedianAgeRule.toDouble()
                item.baseProportion = ((checkAge - medianAge) / addAge) * rule
            }
            else -> item.baseProportion = 0.0
        }
        if (mPlanedSex == 0) item.baseProportion += item.femaleRule.toDouble()
    }

    /**
     * 根据险种选项计算实际费率
     */
    private fun calculateProportion(items: ArrayList<Any>) {
        if (items.isEmpty()) return
        val itemMain = items.first { it is InsuranceModel && it.type == "1" } as InsuranceModel //主险种

        items.filter { it is InsuranceModel }.forEachWithIndex { index, data ->
            data as InsuranceModel  //当前险种

            val itemLrs = ArrayList<CommonData>()
            itemLrs.addItems(data.lrs)

            val itemLds = ArrayList<CommonData>()
            if (data.pinsuranceKindItemIds.isNotEmpty()) {
                val itemIds = data.insuranceKindItemIds
                val itemMainLds = ArrayList<CommonData>()
                itemMainLds.addItems(itemMain.lds) //选项列表里已包含初始值
                itemMainLds.filter { it.type == "0" }.forEach {
                    if (itemIds.contains(it.insuranceOptDictionaryId)) itemLds.add(it)
                }
            }
            @Suppress("UNCHECKED_CAST")
            itemLds.addAll(items.filter { it is CommonData && it.insuredParentPosition == index } as ArrayList<CommonData>)

            val proportion = when (itemLds.size) {
                1 -> {
                    val itemId = itemLds[0].checkItemId
                    itemLrs.firstOrNull { it.item1 == itemId }?.proportion ?: "0.0"
                }
                2 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    itemLrs.firstOrNull { it.item1 == itemId1 && it.item2 == itemId2 }?.proportion
                            ?: "0.0"
                }
                3 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    val itemId3 = itemLds[2].checkItemId

                    itemLrs.firstOrNull {
                        it.item1 == itemId1
                                && it.item2 == itemId2
                                && it.item3 == itemId3
                    }?.proportion ?: "0.0"
                }
                4 -> {
                    val itemId1 = itemLds[0].checkItemId
                    val itemId2 = itemLds[1].checkItemId
                    val itemId3 = itemLds[2].checkItemId
                    val itemId4 = itemLds[3].checkItemId

                    itemLrs.firstOrNull {
                        it.item1 == itemId1
                                && it.item2 == itemId2
                                && it.item3 == itemId3
                                && it.item4 == itemId4
                    }?.proportion ?: "0.0"
                }
                else -> "0.0"
            }

            data.proportion = proportion.toDouble()
        }
    }

    /**
     * 计算保费
     */
    private fun calculateFee(currentPos: Int, parentPos: Int, items: ArrayList<Any>) {
        if (items.isEmpty()) return

        when (items[currentPos]) {
            is CommonData -> {
                calculateProportion(items)

                items.filter { it is InsuranceModel }.forEachWithIndex { index, data ->
                    data as InsuranceModel
                    val proportionSum = data.baseProportion + data.proportion
                    var amount = data.dfInsuredAmount

                    if (items.any {
                                it is InsuranceData
                                        && it.optDictionaryName == "保额"
                                        && it.insuredParentPosition == index
                            }) {
                        amount = (items.first {
                            it is InsuranceData
                                    && it.optDictionaryName == "保额"
                                    && it.insuredParentPosition == index
                        } as InsuranceData).checkName
                    }

                    if (items.any {
                                it is InsuranceData
                                        && it.optDictionaryName == "保费"
                                        && it.insuredParentPosition == index
                            }) {
                        (items.first {
                            it is InsuranceData
                                    && it.optDictionaryName == "保费"
                                    && it.insuredParentPosition == index
                        } as InsuranceData).checkName = if (amount.isEmpty()) ""
                        else DecimalFormat("0.00").format(amount.toDouble() * proportionSum)
                    }

                    if (items.any {
                                it is InsuranceData
                                        && it.optDictionaryName == "份数"
                                        && it.insuredParentPosition == index
                            }) {
                        val itemData = items.first {
                            it is InsuranceData
                                    && it.optDictionaryName == "份数"
                                    && it.insuredParentPosition == index
                        } as InsuranceData

                        if (itemData.checkName.isEmpty()) itemData.optDictionaryFee = ""
                        else {
                            val copiesSum = data.dfcopiesSum.toDouble()
                            val copies = itemData.checkName.toInt()

                            itemData.optDictionaryFee = DecimalFormat("0.00").format(copiesSum * copies * proportionSum)
                        }
                    }
                }
            }
            is InsuranceData -> {
                val itemData = items.first { it is InsuranceModel && it.insuredParentPosition == parentPos } as InsuranceModel
                val item = items[currentPos] as InsuranceData
                when (item.optDictionaryName) {
                    "保额" -> {
                        val itemFee = items.firstOrNull {
                            it is InsuranceData
                                    && it.optDictionaryName == "保费"
                                    && it.insuredParentPosition == parentPos
                        } ?: return
                        itemFee as InsuranceData

                        if (item.checkName.isEmpty()) itemFee.checkName = ""
                        else {
                            val checkTotal = item.checkName.toDouble()
                            val optFeeSum = checkTotal * (itemData.baseProportion + itemData.proportion)
                            itemFee.checkName = DecimalFormat("0.00").format(optFeeSum)
                        }
                    }
                    "保费" -> {
                        val itemTotal = items.firstOrNull {
                            it is InsuranceData
                                    && it.optDictionaryName == "保额"
                                    && it.insuredParentPosition == parentPos
                        } ?: return
                        itemTotal as InsuranceData

                        if (item.checkName.isEmpty()) itemTotal.checkName = ""
                        else {
                            val checkTotal = item.checkName.toDouble()
                            val optTotalSum = checkTotal / (itemData.baseProportion + itemData.proportion)
                            itemTotal.checkName = DecimalFormat("0.00").format(optTotalSum)
                        }
                    }
                    "份数" -> {
                        if (item.checkName.isEmpty()) item.optDictionaryFee = ""
                        else {
                            val copiesSum = itemData.dfcopiesSum.toDouble()
                            val copies = item.checkName.toInt()
                            val optFeeSum = copiesSum * copies * (itemData.baseProportion + itemData.proportion)
                            item.optDictionaryFee = DecimalFormat("0.00").format(optFeeSum)
                        }
                    }
                }
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

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.prospectus_detils)
                .tag(this@PlanMakeActivity)
                .params("prospectusId", intent.getStringExtra("prospectusId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val obj = response.body().`object` ?: return
                        mProductId = obj.productId
                        listKind.addItems(obj.lks)

                        val items = ArrayList<InsuranceModel>()
                        items.addItems(obj.lbs)
                        var kindId = ""
                        listKind.forEach { item ->
                            if (items.any { it.insuranceKindId == item.insuranceKindId }) {
                                kindId = item.insuranceKindId
                                mapKind[item.insuranceKindId] = items
                            }
                        }

                        if (items.isNotEmpty()) {
                            //被保人
                            val data = items.first { it.type == "1" }
                            mStartAge = data.startAge.toInt()
                            mMedianAge = data.medianAge.toInt()
                            mEndAge = data.endAge.toInt()

                            mCheckAge = mMedianAge
                            planed_age.text = "${mMedianAge}岁"
                            val year = Calendar.getInstance().get(Calendar.YEAR)
                            mPlanedYear = year - mCheckAge
                            planed_birth.text = "生日"
                            //投保人
                            if (items.any { it.type == "3" }) {
                                val item = items.first { it.type == "3" }
                                mStartAge1 = item.startAge.toInt()
                                mMedianAge1 = item.medianAge.toInt()
                                mEndAge1 = item.endAge.toInt()
                            } else {
                                mStartAge1 = mStartAge
                                mMedianAge1 = mMedianAge
                                mEndAge1 = mEndAge
                            }

                            mCheckAge1 = mMedianAge1
                            plan_age.text = "${mMedianAge1}岁"
                            mPlanYear = year - mCheckAge1
                            plan_birth.text = "生日"
                            //投保人配偶
                            if (items.any { it.type == "4" }) {
                                val item = items.first { it.type == "4" }
                                mStartAge2 = item.startAge.toInt()
                                mMedianAge2 = item.medianAge.toInt()
                                mEndAge2 = item.endAge.toInt()
                            } else {
                                mStartAge2 = mStartAge
                                mMedianAge2 = mMedianAge
                                mEndAge2 = mEndAge
                            }

                            mCheckAge2 = mMedianAge2
                            plane_age.text = "${mMedianAge2}岁"
                            mPlaneYear = year - mCheckAge2
                            plane_birth.text = "生日"

                            val itemShow = ArrayList<Any>()
                            val itemLbs = ArrayList<InsuranceModel>()

                            itemLbs.addItems(items.filter { data.inshow == "1" })
                            itemShow.addAll(getInsuranceList(itemLbs))

                            if (itemShow.isNotEmpty()) {
                                getCheckedList(itemShow)
                                mapChecked[kindId] = itemShow
                            }
                        }
                    }

                })
    }

    private fun getInsuranceData(kindId: String) {
        OkGo.post<BaseResponse<ArrayList<InsuranceModel>>>(BaseHttp.insurancekind_detils)
                .tag(this@PlanMakeActivity)
                .params("insuranceKindId", kindId)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<InsuranceModel>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<InsuranceModel>>>) {

                        val itemLbs = ArrayList<InsuranceModel>()
                        itemLbs.addItems(response.body().`object`)
                        mapKind[kindId] = itemLbs

                        showInsuranceDialog(kindId, getInsuranceList(itemLbs))
                    }

                })
    }
}
