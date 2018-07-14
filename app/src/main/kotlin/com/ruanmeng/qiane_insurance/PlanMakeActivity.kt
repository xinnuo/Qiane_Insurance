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
    private var isHomeInsurance = false //是否家庭版
    //被保人
    private var mPlanedSex = 1
    private var mPlanedYear: Int = 0
    private var mPlanedMonth: Int = 0
    private var mPlanedDay: Int = 0
    private var mStartAge = 0
    private var mMedianAge = 0
    private var mEndAge = 0
    //投保人
    private var mPlanSex = 1
    private var mPlanYear: Int = 0
    private var mPlanMonth: Int = 0
    private var mPlanDay: Int = 0
    private var mStartAge1 = 0
    private var mMedianAge1 = 0
    private var mEndAge1 = 0
    //投保人配偶
    private var mPlaneSex = 0
    private var mPlaneYear: Int = 0
    private var mPlaneMonth: Int = 0
    private var mPlaneDay: Int = 0
    private var mStartAge2 = 0
    private var mMedianAge2 = 0
    private var mEndAge2 = 0

    private var mPlanerSex = 1
    private var mFocusPosition = -1

    @SuppressLint("HandlerLeak")
    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 1, 2 -> { //性别，年龄，生日
                    if (listChecked.isEmpty()) return

                    if (!isHomeInsurance) {
                        Observable.create<Int> {
                            updateCheckedList()
                            it.onNext(msg.what)
                        }
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { updateList() }
                    } else updateHomeCheckedList(msg.what)
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
                                    if (!isHomeInsurance) {
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
                                    } else {
                                        Flowable.just<String>(data.insuranceKindId)
                                                .map { return@map mapChecked[it]!! }
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe { showHomeInsuranceDialog(it) }
                                    }
                                }

                                .clicked(R.id.item_plan_del) {
                                    val index = listChecked.indexOf(data)
                                    listChecked.remove(data)
                                    mapChecked.remove(data.insuranceKindId)
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
                obj.put("coverAge", mMedianAge)
                obj.put("coverBirthday", if (planed_birth.text.toString() == "生日") "" else planed_birth.text.toString())
                obj.put("coverSex", mPlanedSex)
                obj.put("throwAge", mMedianAge1)
                obj.put("throwBirthday", if (plan_birth.text.toString() == "生日") "" else plan_birth.text.toString())
                obj.put("throwSex", mPlanSex)
                obj.put("throwWifeAge", mMedianAge2)
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

                if (isHomeInsurance) {
                    val objLhs = JSONArray()
                    val kindId = listChecked[0].insuranceKindId
                    val items = mapChecked[kindId]!!

                    items.filter { it is InsuranceHomeModel }.forEachWithIndex { index, data ->
                        data as InsuranceHomeModel
                        val objItem = JSONObject()
                        if (data.isChecked) {
                            objItem.put("premium", data.premium)

                            if (index == 0 || items.none {
                                        it is InsuranceData
                                                && it.insuranceOptDictionaryId == "10"
                                                && it.insuredParentPosition == index
                                    }) objItem.put("guarantorAge", mMedianAge)
                            else {
                                val ageInner = (items.first {
                                    it is InsuranceData
                                            && it.insuranceOptDictionaryId == "10"
                                            && it.insuredParentPosition == index
                                } as InsuranceData).checkItemId

                                objItem.put("guarantorAge", ageInner)
                            }

                            if (index == 0 || items.none {
                                        it is InsuranceData
                                                && it.insuranceOptDictionaryId == "11"
                                                && it.insuredParentPosition == index
                                    }) objItem.put("guarantorSex", mPlanedSex)
                            else {
                                val sexInner = (items.first {
                                    it is InsuranceData
                                            && it.insuranceOptDictionaryId == "11"
                                            && it.insuredParentPosition == index
                                } as InsuranceData).checkItemId

                                objItem.put("guarantorSex", if (sexInner == "女") 0 else 1)
                            }

                            objLhs.put(objItem)
                        }
                    }

                    obj.put("lhs", objLhs)
                }

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
                        mMedianAge - mStartAge,
                        items) { position, name ->
                    mMedianAge = mStartAge + position
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
                        mMedianAge1 - mStartAge1,
                        items) { position, name ->
                    mMedianAge1 = mStartAge1 + position
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
                        mMedianAge2 - mStartAge2,
                        items) { position, name ->
                    mMedianAge2 = mStartAge2 + position
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

                    mMedianAge = year - y
                    planed_age.text = "${mMedianAge}岁"
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

                    mMedianAge1 = year - y
                    plan_age.text = "${mMedianAge1}岁"
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

                    mMedianAge2 = year - y
                    plane_age.text = "${mMedianAge2}岁"
                    plane_birth.text = date

                    mHandler.sendEmptyMessage(2)
                }
            }
            R.id.plan_host -> {
                if (mapKind.isEmpty()) {
                    showToast("数据加载失败")
                    return
                }

                if (isHomeInsurance) {
                    if (listChecked.isNotEmpty()) return

                    Flowable.just(listKind[0].insuranceKindId)
                            .map {
                                val items = ArrayList<InsuranceModel>()
                                items.addItems(mapKind[it])
                                getHomeInsuranceList(items)
                            }
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { showHomeInsuranceDialog(it) }
                } else showOptionDialog()
            }
        }
    }

    /**
     * 显示险种列表框
     */
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

                                            val ageStart = data.startAge.toInt()
                                            val ageEnd = data.endAge.toInt()

                                            if (mMedianAge !in ageStart..ageEnd) {
                                                showToast("当前所选年龄不符合该险种年龄要求")
                                                return@clicked
                                            }

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

    /**
     * 显示险种选择框
     */
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

                                            if (itemStr.size < 2) return@clicked

                                            DialogHelper.showItemDialog(
                                                    baseContext,
                                                    "选择${data.optDictionaryName}",
                                                    itemStr) { position, name ->

                                                data.checkName = name
                                                data.checkItemId = options[position].insuranceItemId

                                                //选项id联动
                                                val linkId = item.pitems
                                                if (linkId.contains(data.insuranceOptDictionaryId)) {
                                                    if (item.cascade == "1") {
                                                        window.decorView.postDelayed({
                                                            OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.cascadeItems_items_list)
                                                                    .tag(this@PlanMakeActivity)
                                                                    .params("insuranceKindId", item.insuranceKindId)
                                                                    .params("itemId", options[position].insuranceItemId)
                                                                    .params("optId", data.insuranceOptDictionaryId)
                                                                    .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                                                                        override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                                                                            val itemLrs = ArrayList<CommonData>()
                                                                            itemLrs.addItems(response.body().`object`)
                                                                            val linkids = linkId.split(",")

                                                                            linkids.forEach { inner ->
                                                                                if (inner == data.insuranceOptDictionaryId) return@forEach

                                                                                val itemInner = items.first {
                                                                                    it is CommonData
                                                                                            && it.insuranceOptDictionaryId == inner
                                                                                            && it.insuredParentPosition == data.insuredParentPosition
                                                                                } as CommonData

                                                                                if (itemLrs.none { it.insuranceItemId == itemInner.checkItemId }) {
                                                                                    val dataLrs = itemLrs.first {
                                                                                        it.insuranceOptDictionaryId == itemInner.insuranceOptDictionaryId
                                                                                    }

                                                                                    itemInner.checkName = dataLrs.itemName
                                                                                    itemInner.checkItemId = dataLrs.insuranceItemId
                                                                                }
                                                                            }

                                                                            Observable.create<Int> {
                                                                                val index = items.indexOf(data)
                                                                                calculateProportion(items)
                                                                                calculateFee(index, data.insuredParentPosition, items)
                                                                                it.onNext(index)
                                                                            }
                                                                                    .subscribeOn(Schedulers.newThread())
                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                    .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                                                        }

                                                                    })
                                                        }, 300)
                                                    } else {
                                                        Observable.create<Int> {
                                                            val index = items.indexOf(data)
                                                            calculateProportion(items)
                                                            calculateFee(index, data.insuredParentPosition, items)
                                                            it.onNext(index)
                                                        }
                                                                .subscribeOn(Schedulers.newThread())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                                    }
                                                } else {
                                                    (adapter as SlimAdapter).notifyDataSetChanged()
                                                }
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
     * 获取险种列表
     */
    private fun getInsuranceList(itemLbs: ArrayList<InsuranceModel>): ArrayList<Any> {
        val items = ArrayList<Any>()
        itemLbs.forEachWithIndex { index, it ->
            it.isClickable = it.ichange == "1"
            it.isExpand = it.inshow == "1"
            it.isChecked = it.pitchOn == "1"
            it.isGray = !it.isChecked && !it.isClickable
            val ageStart = it.startAge.toInt()
            val ageEnd = it.endAge.toInt()
            when (it.type) {
                "1", "2" -> if (mMedianAge !in ageStart..ageEnd) {
                    it.isClickable = false
                    it.isExpand = false
                    it.isChecked = false
                    it.isGray = true
                }
                "3" -> if (mMedianAge1 !in ageStart..ageEnd) {
                    it.isClickable = false
                    it.isExpand = false
                    it.isChecked = false
                    it.isGray = true
                }
                "4" -> if (mMedianAge2 !in ageStart..ageEnd) {
                    it.isClickable = false
                    it.isExpand = false
                    it.isChecked = false
                    it.isGray = true
                }
            }
            it.insuredParentPosition = index

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

        val indexPosition = items.indexOfFirst { it is CommonData }
        if (indexPosition > -1) calculateFee(indexPosition, 0, items)

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
                        isDelete = !(inner.first {
                            //默认显示的险种不可删除
                            it is InsuranceModel
                                    && it.type == "1"
                        } as InsuranceModel).isExpand
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

                        when (data.proportionType) {
                            "0" -> {
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
                            }
                            "1" -> {
                                if (data.insuredAmount > 0)
                                    itemAmount = DecimalFormat("0.00").format(data.insuredAmount)
                                itemFee = DecimalFormat("0.00").format(data.premium)
                                kindFee += data.premium
                            }
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

            val itemData = ArrayList<Any>()
            itemData.addItems(mapChecked[item.insuranceKindId])
            calculateProportion(itemData)

            itemTable.clear()
            itemTable.add(TableData("险种", "保额", "保费", "交费期间"))

            items.forEach {
                if (it.insuranceItem.proportionType == "1") {
                    val amount = it.insuranceItem.insuredAmount
                    it.insuredAmount = if (amount > 0) amount.toString() else "-"
                    it.insuredFee = DecimalFormat("0.00").format(it.insuranceItem.premium)
                } else {
                    val fee = it.insuredAmount.toDouble() * it.insuranceItem.proportion
                    it.insuredFee = DecimalFormat("0.00").format(fee)
                }
                totalFee += it.insuredFee.toDouble()

                itemTable.add(TableData(it.insuranceItem.insuranceKindName, it.insuredAmount, it.insuredFee, it.insuredRange))
            }

            item.insuredTotalFee = totalFee
        }
    }

    /**
     * 根据险种选项计算费率
     */
    private fun calculateProportion(items: ArrayList<Any>) {
        if (items.isEmpty()) return
        val itemMain = items.first { it is InsuranceModel && it.type == "1" } as InsuranceModel //主险种

        items.filter { it is InsuranceModel }.forEachWithIndex { index, data ->
            data as InsuranceModel  //当前险种

            //费率列表
            val itemLrs = ArrayList<CommonData>()
            itemLrs.addItems(data.lrs)

            //选项联动id
            val linkId = data.pitems
            if (linkId.isEmpty()) return@forEachWithIndex

            //获取选项联动的id值
            val linkIds = linkId.split(",")
            val linkCheckIds = ArrayList<String>()
            linkIds.forEach { item ->
                when (item) {
                    "10" -> when (data.type) {
                        "1", "2" -> linkCheckIds.add(mMedianAge.toString())
                        "3" -> linkCheckIds.add(mMedianAge1.toString())
                        "4" -> linkCheckIds.add(mMedianAge2.toString())
                    }
                    "11" -> when (data.type) {
                        "1", "2" -> linkCheckIds.add(if (mPlanedSex == 0) "女" else "男")
                        "3" -> linkCheckIds.add(if (mPlanSex == 0) "女" else "男")
                        "4" -> linkCheckIds.add(if (mPlaneSex == 0) "女" else "男")
                    }
                    else -> {
                        val itemIds = data.insuranceKindItemIds
                        if (itemIds.contains(item)) { //从主险获取
                            val itemMainLds = ArrayList<CommonData>()
                            itemMainLds.addItems(itemMain.lds) //选项列表里已包含初始值
                            linkCheckIds.add(itemMainLds.first {
                                it.type == "0" && it.insuranceOptDictionaryId == item
                            }.checkItemId)
                        } else {
                            val innerData = items.find {
                                it is CommonData
                                        && it.insuredParentPosition == index
                                        && it.insuranceOptDictionaryId == item
                            } as CommonData
                            linkCheckIds.add(innerData.checkItemId)
                        }
                    }
                }
            }

            //获取费率
            val dataLrs = when (linkCheckIds.size) {
                1 -> itemLrs.firstOrNull { it.item1 == linkCheckIds[0] }
                2 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                }
                3 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                }
                4 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                }
                5 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                            && it.item5 == linkCheckIds[4]
                }
                6 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                            && it.item5 == linkCheckIds[4]
                            && it.item6 == linkCheckIds[5]
                }
                else -> null
            } ?: return@forEachWithIndex

            data.proportionType = dataLrs.type
            if (dataLrs.type == "1") {
                data.insuredAmount = if (dataLrs.insuredAmount.isEmpty()) 0.0 else dataLrs.insuredAmount.toDouble()
                data.premium = if (dataLrs.premium.isEmpty()) 0.0 else dataLrs.premium.toDouble()
            } else data.proportion = dataLrs.proportion.toDouble()
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

                    if (items.none {
                                it is InsuranceData
                                        && it.insuredParentPosition == index
                            }) return@forEachWithIndex

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
                        else DecimalFormat("0.00").format(amount.toDouble() * data.proportion)
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

                            itemData.optDictionaryFee = DecimalFormat("0.00").format(copiesSum * copies * data.proportion)
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
                            val optFeeSum = checkTotal * itemData.proportion
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
                            val optTotalSum = checkTotal / itemData.proportion
                            itemTotal.checkName = DecimalFormat("0.00").format(optTotalSum)
                        }
                    }
                    "份数" -> {
                        if (item.checkName.isEmpty()) item.optDictionaryFee = ""
                        else {
                            val copiesSum = itemData.dfcopiesSum.toDouble()
                            val copies = item.checkName.toInt()
                            val optFeeSum = copiesSum * copies * itemData.proportion
                            item.optDictionaryFee = DecimalFormat("0.00").format(optFeeSum)
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取家庭版险种列表
     */
    private fun getHomeInsuranceList(itemLbs: ArrayList<InsuranceModel>): ArrayList<Any> {
        val items = ArrayList<Any>()
        if (itemLbs.isEmpty()) return items

        val item = itemLbs[0]
        val count = item.maxpeple.toInt()
        (0 until count).forEach { index ->
            items.add(InsuranceHomeModel().apply {
                itemName = "家庭成员${index + 1}"
                insuranceKindId = item.insuranceKindId
                insuredParentPosition = index
                isClickable = index != 0
                isChecked = index == 0
            })

            if (index == 0) {
                val itemLds = ArrayList<CommonData>()
                val itemLis = ArrayList<CommonData>()
                itemLds.addItems(item.lds)
                itemLis.addItems(item.lis)

                itemLds.filterNot {
                    it.insuranceOptDictionaryId == "10"
                            || it.insuranceOptDictionaryId == "11"
                }.forEach { inner ->
                    val itemData = InsuranceData()
                    itemData.optDictionaryName = inner.optDictionaryName
                    itemData.insuranceOptDictionaryId = inner.insuranceOptDictionaryId

                    if (itemLis.any {
                                it.checkItem == "1"
                                        && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                            }) {
                        val data = itemLis.first {
                            it.checkItem == "1"
                                    && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                        }

                        itemData.insuranceKindId = item.insuranceKindId
                        itemData.checkItemId = data.insuranceItemId
                        itemData.checkName = data.itemName
                    }
                    itemData.insuredParentPosition = index
                    items.add(itemData)
                }
            }
        }

        calculateHomeFee(item.insuranceKindId, items)

        return items
    }

    /**
     * 显示家庭版险种选择框
     */
    private fun showHomeInsuranceDialog(items: ArrayList<Any>) {
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
                            .register<InsuranceHomeModel>(R.layout.item_plan_dialog_list) { data, injector ->
                                val isFirst = items.indexOf(data) == 0
                                val isLast = items.indexOf(data) == items.size - 1

                                injector.text(R.id.item_plan_check, data.itemName)
                                        .checked(R.id.item_plan_check, data.isChecked)
                                        .visibility(R.id.item_plan_divider1, if (isLast) View.GONE else View.VISIBLE)
                                        .visibility(R.id.item_plan_divider2, if (!isLast) View.GONE else View.VISIBLE)
                                        .visibility(R.id.item_plan_divider3, if (!isFirst) View.GONE else View.VISIBLE)

                                        .clicked(R.id.item_plan) {
                                            if (data.isClickable) {
                                                data.isChecked = !data.isChecked

                                                Observable.create<Int> {
                                                    val index = items.indexOf(data)
                                                    updateHomeInsuranceList(items.indexOf(data), data.insuranceKindId, items)
                                                    it.onNext(index)
                                                }
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                            }
                                        }
                            }
                            .register<InsuranceData>(R.layout.item_plan_dialog_type1_list) { data, injector ->
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

                                            val itemList = mapKind[data.insuranceKindId]
                                                    ?: return@clicked
                                            val item = itemList[0]

                                            when (data.insuranceOptDictionaryId) {
                                                "10" -> {
                                                    val ageStart = item.startAge.toInt()
                                                    val ageEnd = item.endAge.toInt()
                                                    for (i in ageStart..ageEnd) itemStr.add("${i}岁")
                                                }
                                                "11" -> {
                                                    itemStr.add("男")
                                                    itemStr.add("女")
                                                }
                                                else -> {
                                                    options.addItems(item.lis?.filter {
                                                        it.insuranceOptDictionaryId == data.insuranceOptDictionaryId
                                                    }).forEach { itemStr.add(it.itemName) }
                                                }
                                            }

                                            if (itemStr.size < 2) return@clicked

                                            DialogHelper.showItemDialog(
                                                    baseContext,
                                                    "选择${data.optDictionaryName}",
                                                    itemStr) { position, name ->

                                                when (data.insuranceOptDictionaryId) {
                                                    "10" -> {
                                                        data.checkName = name
                                                        data.checkItemId = name.replace("岁", "")
                                                    }
                                                    "11" -> {
                                                        data.checkName = name
                                                        data.checkItemId = name
                                                    }
                                                    else -> {
                                                        data.checkName = name
                                                        data.checkItemId = options[position].insuranceItemId
                                                    }
                                                }

                                                //选项id联动
                                                val linkId = item.pitems
                                                if (linkId.contains(data.insuranceOptDictionaryId)) {
                                                    Observable.create<Int> {
                                                        val index = items.indexOf(data)
                                                        calculateHomeFee(data.insuranceKindId, items)
                                                        it.onNext(index)
                                                    }
                                                            .subscribeOn(Schedulers.newThread())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe { (adapter as SlimAdapter).notifyDataSetChanged() }
                                                } else {
                                                    (adapter as SlimAdapter).notifyDataSetChanged()
                                                }
                                            }
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
                            is InsuranceData -> if (item.checkName.isEmpty()) {
                                showToast("请选择${item.optDictionaryName}")
                                return@onClick
                            }
                        }
                    }

                    dismiss()

                    getHomeCheckedList(items)
                    mapChecked[listKind[0].insuranceKindId] = items
                }

                return view
            }

        }
        dialog.show()
    }

    /**
     * 更新家庭版险种列表
     */
    private fun updateHomeInsuranceList(currentPos: Int, kindId: String, items: ArrayList<Any>) {
        val itemData = items[currentPos] as InsuranceHomeModel

        if (itemData.isChecked) {
            val itemLbs = ArrayList<Any>()
            val itemType = ArrayList<CommonData>()
            val item = mapKind[kindId] ?: return
            val kindItemIds = item[0].pinsuranceKindItemIds
            itemType.addItems(item[0].lds)

            itemType.forEach { inner ->
                if (kindItemIds.contains(inner.insuranceOptDictionaryId)) {
                    val itemLis = ArrayList<CommonData>()
                    itemLis.addItems(item[0].lis)

                    val innerData = InsuranceData()
                    innerData.optDictionaryName = inner.optDictionaryName
                    innerData.insuranceOptDictionaryId = inner.insuranceOptDictionaryId

                    if (itemLis.any {
                                it.checkItem == "1"
                                        && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                            }) {
                        val data = itemLis.first {
                            it.checkItem == "1"
                                    && it.insuranceOptDictionaryId == inner.insuranceOptDictionaryId
                        }

                        innerData.checkItemId = data.insuranceItemId
                        innerData.checkName = data.itemName
                    }

                    if (inner.insuranceOptDictionaryId == "10") {
                        innerData.checkItemId = item[0].medianAge
                        innerData.checkName = "${item[0].medianAge}岁"
                    }

                    if (inner.insuranceOptDictionaryId == "11") {
                        innerData.checkItemId = "男"
                        innerData.checkName = "男"
                    }

                    innerData.insuranceKindId = itemData.insuranceKindId
                    innerData.insuredParentPosition = itemData.insuredParentPosition
                    itemLbs.add(innerData)
                }
            }

            items.addAll(currentPos + 1, itemLbs)

            calculateHomeFee(kindId, items)
        } else {
            items.removeAll {
                it is InsuranceData
                        && it.insuredParentPosition == itemData.insuredParentPosition
            }
        }
    }

    /**
     * 根据险种选项计算家庭版保额、保费
     */
    private fun calculateHomeFee(kindId: String, items: ArrayList<Any>) {
        if (items.isEmpty()) return
        val itemList = mapKind[kindId] ?: return
        val itemMain = itemList[0]

        //选项联动id
        val linkId = itemMain.pitems
        if (linkId.isEmpty()) return

        items.filter { it is InsuranceHomeModel }.forEachWithIndex { index, data ->
            data as InsuranceHomeModel  //当前险种

            if (items.none {
                        it is InsuranceData
                                && it.insuredParentPosition == index
                    }) return@forEachWithIndex

            //费率列表
            val itemLrs = ArrayList<CommonData>()
            itemLrs.addItems(itemMain.lrs)

            //获取选项联动的id值
            val linkIds = linkId.split(",")
            val linkCheckIds = ArrayList<String>()
            linkIds.forEach { item ->
                when (item) {
                    "10" -> {
                        if (index == 0) linkCheckIds.add(mMedianAge.toString())
                        else {
                            if (items.none {
                                        it is InsuranceData
                                                && it.insuredParentPosition == index
                                                && it.insuranceOptDictionaryId == item
                                    }) {
                                val innerData = items.find {
                                    it is InsuranceData
                                            && it.insuredParentPosition == 0
                                            && it.insuranceOptDictionaryId == item
                                } as InsuranceData
                                linkCheckIds.add(innerData.checkItemId)
                            } else {
                                val innerData = items.find {
                                    it is InsuranceData
                                            && it.insuredParentPosition == index
                                            && it.insuranceOptDictionaryId == item
                                } as InsuranceData
                                linkCheckIds.add(innerData.checkItemId)
                            }
                        }
                    }
                    "11" -> {
                        if (index == 0) linkCheckIds.add(if (mPlanedSex == 0) "女" else "男")
                        else {
                            if (items.none {
                                        it is InsuranceData
                                                && it.insuredParentPosition == index
                                                && it.insuranceOptDictionaryId == item
                                    }) {
                                val innerData = items.find {
                                    it is InsuranceData
                                            && it.insuredParentPosition == 0
                                            && it.insuranceOptDictionaryId == item
                                } as InsuranceData
                                linkCheckIds.add(innerData.checkItemId)
                            } else {
                                val innerData = items.find {
                                    it is InsuranceData
                                            && it.insuredParentPosition == index
                                            && it.insuranceOptDictionaryId == item
                                } as InsuranceData
                                linkCheckIds.add(innerData.checkItemId)
                            }
                        }
                    }
                    else -> {
                        if (items.none {
                                    it is InsuranceData
                                            && it.insuredParentPosition == index
                                            && it.insuranceOptDictionaryId == item
                                }) {
                            val innerData = items.find {
                                it is InsuranceData
                                        && it.insuredParentPosition == 0
                                        && it.insuranceOptDictionaryId == item
                            } as InsuranceData
                            linkCheckIds.add(innerData.checkItemId)
                        } else {
                            val innerData = items.find {
                                it is InsuranceData
                                        && it.insuredParentPosition == index
                                        && it.insuranceOptDictionaryId == item
                            } as InsuranceData
                            linkCheckIds.add(innerData.checkItemId)
                        }
                    }
                }
            }

            val dataLrs = when (linkCheckIds.size) {
                1 -> itemLrs.firstOrNull { it.item1 == linkCheckIds[0] }
                2 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                }
                3 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                }
                4 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                }
                5 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                            && it.item5 == linkCheckIds[4]
                }
                6 -> itemLrs.firstOrNull {
                    it.item1 == linkCheckIds[0]
                            && it.item2 == linkCheckIds[1]
                            && it.item3 == linkCheckIds[2]
                            && it.item4 == linkCheckIds[3]
                            && it.item5 == linkCheckIds[4]
                            && it.item6 == linkCheckIds[5]
                }
                else -> null
            } ?: return@forEachWithIndex

            if (dataLrs.type == "1") {
                data.insuredAmount = if (dataLrs.insuredAmount.isEmpty()) 0.0 else dataLrs.insuredAmount.toDouble()
                data.premium = if (dataLrs.premium.isEmpty()) 0.0 else dataLrs.premium.toDouble()
            }
        }
    }

    /**
     * 获取家庭版已选险种列表
     */
    private fun getHomeCheckedList(items: ArrayList<Any>) {
        Flowable.just<ArrayList<Any>>(items)
                .map { inner ->
                    val kindId = (items[0] as InsuranceHomeModel).insuranceKindId
                    val itemMain = mapKind[kindId]!![0]
                    val kindName = itemMain.insuranceKindName
                    val item = InsuranceData().apply {
                        insuranceKindId = kindId
                        insuranceKindName = kindName
                        insuredTable.add(TableData("险种", "保额", "保费", "交费期间"))
                    }

                    var itemAmount = 0.0
                    var itemFee = 0.0
                    var itemRange = "-"
                    var itemPeriod = "-"

                    inner.filter { it is InsuranceHomeModel }.forEach { data ->
                        data as InsuranceHomeModel
                        if (!data.isChecked) return@forEach

                        itemAmount += data.insuredAmount
                        itemFee += data.premium

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "交费期间"
                                            && it.insuredParentPosition == 0
                                }) {
                            itemRange = (inner.first {
                                it is InsuranceData
                                        && it.optDictionaryName == "交费期间"
                                        && it.insuredParentPosition == 0
                            } as InsuranceData).checkName
                        }

                        if (inner.any {
                                    it is InsuranceData
                                            && it.optDictionaryName == "保险期间"
                                            && it.insuredParentPosition == 0
                                }) {
                            itemPeriod = (inner.first {
                                it is InsuranceData
                                        && it.optDictionaryName == "保险期间"
                                        && it.insuredParentPosition == 0
                            } as InsuranceData).checkName
                        }
                    }

                    val totalAmount = DecimalFormat("0.00").format(itemAmount)
                    val totalFee = DecimalFormat("0.00").format(itemFee)
                    item.insuredTable.add(TableData(kindName, totalAmount, totalFee, itemRange))
                    item.insuredItem.add(InsuranceItemData(itemMain, totalAmount, totalFee, itemPeriod, itemRange))
                    item.insuredTotalFee = itemFee

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
    private fun updateHomeCheckedList(type: Int) {
        val kindId = listChecked[0].insuranceKindId
        val itemMain = mapKind[kindId]?.get(0) ?: return
        val items = mapChecked[kindId] ?: return
        val linkId = itemMain.pitems

        when (type) {
            0 -> if ("11" in linkId) {
                calculateHomeFee(kindId, items)
                getHomeCheckedList(items)
            }
            1, 2 -> if ("10" in linkId) {
                calculateHomeFee(kindId, items)
                getHomeCheckedList(items)
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

                        //被保人
                        mStartAge = obj.coverStartAge.toInt()
                        mMedianAge = obj.dfCoverAge.toInt()
                        mEndAge = obj.coverEndAge.toInt()

                        planed_age.text = "${mMedianAge}岁"
                        val year = Calendar.getInstance().get(Calendar.YEAR)
                        mPlanedYear = year - mMedianAge
                        planed_birth.text = "生日"

                        //投保人
                        mStartAge1 = obj.startAge.toInt()
                        mMedianAge1 = obj.dfAge.toInt()
                        mEndAge1 = obj.endAge.toInt()

                        plan_age.text = "${mMedianAge1}岁"
                        mPlanYear = year - mMedianAge1
                        plan_birth.text = "生日"

                        //投保人配偶
                        mStartAge2 = obj.spouseStartAge.toInt()
                        mMedianAge2 = obj.dfSpouseAge.toInt()
                        mEndAge2 = obj.spouseEndAge.toInt()

                        plane_age.text = "${mMedianAge2}岁"
                        mPlaneYear = year - mMedianAge2
                        plane_birth.text = "生日"

                        if (items.isNotEmpty()) {
                            val kindData = items.first { it.type == "1" }
                            mapKind[kindData.insuranceKindId] = items

                            if (items.size == 1 && kindData.homes == "1") {
                                isHomeInsurance = true
                                return
                            }

                            //默认显示
                            val itemShow = ArrayList<Any>()
                            val itemLbs = ArrayList<InsuranceModel>()

                            itemLbs.addItems(items.filter { it.inshow == "1" })
                            itemShow.addAll(getInsuranceList(itemLbs))

                            if (itemShow.isNotEmpty()) {
                                getCheckedList(itemShow)
                                mapChecked[kindData.insuranceKindId] = itemShow
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
