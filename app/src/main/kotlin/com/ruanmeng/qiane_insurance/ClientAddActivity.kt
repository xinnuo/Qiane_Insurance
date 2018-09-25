package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.BankMessageEvent
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NameLengthFilter
import com.ruanmeng.utils.bankCardReplaceHeaderWithStar
import com.ruanmeng.view.FullyLinearLayoutManager
import com.weigan.loopview.LoopView
import kotlinx.android.synthetic.main.activity_client_add.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ClientAddActivity : BaseActivity() {

    private val listTel = ArrayList<CommonData>()
    private val listEmail = ArrayList<CommonData>()
    private val listLicense = ArrayList<CommonData>()
    private val listAddress = ArrayList<CommonData>()
    private var listProvince = ArrayList<CommonData>()
    private var listCity = ArrayList<CommonData>()
    private var listDistrict = ArrayList<CommonData>()

    private lateinit var mTitle: String
    private var customerSex = 1
    private var usercustomerId = ""
    private var cardNumber = ""
    private var cardBank = ""
    private var cardMobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_add)
        mTitle = intent.getStringExtra("title")
        init_title(mTitle, when (mTitle) {
            "添加客户" -> "完成"
            "客户信息" -> "修改"
            else -> ""
        })

        EventBus.getDefault().register(this@ClientAddActivity)

        val userName = intent.getStringExtra("name") ?: ""
        val userBirth = intent.getStringExtra("birth") ?: ""
        val userPhone = intent.getStringExtra("phone") ?: ""
        val userEmail = intent.getStringExtra("email") ?: ""

        if (userName.isNotEmpty()) {
            et_name.setText(userName)
            et_name.setSelection(et_name.text.length)
        }
        if (userBirth.isNotEmpty()) client_birth.text = userBirth
        if (userPhone.isNotEmpty()) {
            val items = userPhone.split(",")
            items.forEach { listTel.add(CommonData().apply { contact = it }) }
        }
        if (userEmail.isNotEmpty()) {
            val items = userEmail.split(",")
            items.forEach { listEmail.add(CommonData().apply { contact = it }) }
        }

        (client_tel_list.adapter as SlimAdapter).updateData(listTel)
        (client_email_list.adapter as SlimAdapter).updateData(listEmail)
        (client_license_list.adapter as SlimAdapter).updateData(listLicense)
        (client_addr_list.adapter as SlimAdapter).updateData(listAddress)

        if (mTitle == "客户信息") {
            usercustomerId = intent.getStringExtra("usercustomerId")
            getData()
        }
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(16))

        client_tel_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_tel_list) { data, injector ->
                        injector.text(R.id.item_tel_input, data.contact)

                                .with<EditText>(R.id.item_tel_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.contact)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.contact = s.toString()
                                        }

                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                    }

                                    it.addTextChangedListener(textWatcher)
                                    it.tag = textWatcher
                                }

                                .clicked(R.id.item_tel_close) {
                                    val index = listTel.indexOf(data)
                                    listTel.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_email_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_email_list) { data, injector ->
                        injector.text(R.id.item_email_input, data.contact)

                                .with<EditText>(R.id.item_email_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.contact)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.contact = s.toString()
                                        }

                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                    }

                                    it.addTextChangedListener(textWatcher)
                                    it.tag = textWatcher
                                }

                                .clicked(R.id.item_email_close) {
                                    val index = listEmail.indexOf(data)
                                    listEmail.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_license_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_license_list) { data, injector ->

                        val index = listLicense.indexOf(data)

                        injector.text(R.id.item_license_type, data.certificateType)
                                .text(R.id.item_license_start, data.certificateStartDate)
                                .text(R.id.item_license_end, data.certificateEndDate)
                                .checked(R.id.item_license_check, data.longValid == "1")
                                .with<EditText>(R.id.item_license_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.certificateNumber)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.certificateNumber = s.toString()
                                        }

                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                    }

                                    it.addTextChangedListener(textWatcher)
                                    it.tag = textWatcher
                                }

                                .clicked(R.id.item_license_type_ll) {
                                    val items = listOf("身份证", "军官证", "护照")
                                    DialogHelper.showItemDialog(
                                            baseContext,
                                            "选择证件类型",
                                            items) { _, name ->

                                        data.certificateType = name
                                        (this.adapter as SlimAdapter).notifyItemChanged(index)
                                    }
                                }

                                .clicked(R.id.item_license_start_ll) {
                                    val year = Calendar.getInstance().get(Calendar.YEAR)
                                    DialogHelper.showDateDialog(
                                            baseContext,
                                            year - 50,
                                            year,
                                            3,
                                            "选择起始日期",
                                            true,
                                            false) { _, _, _, _, _, date ->

                                        data.certificateStartDate = date
                                        (this.adapter as SlimAdapter).notifyItemChanged(index)
                                    }
                                }

                                .clicked(R.id.item_license_end_ll) {
                                    val year = Calendar.getInstance().get(Calendar.YEAR)
                                    DialogHelper.showDateDialog(
                                            baseContext,
                                            year,
                                            year + 50,
                                            3,
                                            "选择结束日期",
                                            true,
                                            false) { _, _, _, _, _, date ->

                                        data.certificateEndDate = date
                                        (this.adapter as SlimAdapter).notifyItemChanged(index)
                                    }
                                }

                                .clicked(R.id.item_license_ever) {
                                    data.longValid = if (data.longValid == "1") "0" else "1"
                                    (this.adapter as SlimAdapter).notifyItemChanged(index)
                                }

                                .clicked(R.id.item_license_close) {
                                    listLicense.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_addr_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_address_list) { data, injector ->

                        val index = listAddress.indexOf(data)

                        injector.text(R.id.item_address_type, data.province + data.city + data.district)

                                .with<EditText>(R.id.item_address_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.detailAdress)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.detailAdress = s.toString()
                                        }

                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                    }

                                    it.addTextChangedListener(textWatcher)
                                    it.tag = textWatcher
                                }

                                .with<EditText>(R.id.item_address_code) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.postcode)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.postcode = s.toString()
                                        }

                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                    }

                                    it.addTextChangedListener(textWatcher)
                                    it.tag = textWatcher
                                }

                                .clicked(R.id.item_address_type_ll) { _ ->
                                    showLoadingDialog()

                                    getProvince(object : ResultCallBack{

                                        override fun doWork() {
                                            cancelLoadingDialog()

                                            DialogHelper.showAddressDialog(
                                                    baseContext,
                                                    "选择所在地区",
                                                    listProvince,
                                                    listCity,
                                                    listDistrict,
                                                    object : DialogHelper.AddressCallBack {

                                                        override fun doWork(pos_province: Int, pos_city: Int, pos_district: Int) {
                                                            data.province = listProvince[pos_province].areaName
                                                            data.city = if (listCity.isEmpty()) "" else listCity[pos_city].areaName
                                                            data.district = if (listDistrict.isEmpty()) "" else listDistrict[pos_district].areaName

                                                            (this@apply.adapter as SlimAdapter).notifyItemRemoved(index)
                                                        }

                                                        override fun getCities(loopView: LoopView, loopView2: LoopView, pos: Int) {
                                                            getCity(listProvince[pos].areaName, object : ResultCallBack{

                                                                override fun doWork() {
                                                                    val cities = ArrayList<String>()
                                                                    val districts = ArrayList<String>()

                                                                    listCity.mapTo(cities) { it.areaName }
                                                                    listDistrict.mapTo(districts) { it.areaName }

                                                                    if (cities.isNotEmpty()) loopView.setItems(cities)
                                                                    if (districts.isNotEmpty()) {
                                                                        loopView2.visible()
                                                                        loopView2.setItems(districts)
                                                                    } else loopView2.invisible()
                                                                }

                                                            })
                                                        }

                                                        override fun getDistricts(loopView: LoopView, pos: Int) {
                                                            getDistrict(listCity[pos].areaName, object : ResultCallBack{

                                                                override fun doWork() {
                                                                    val districts = ArrayList<String>()
                                                                    listDistrict.mapTo(districts) { it.areaName }

                                                                    if (districts.isNotEmpty()) {
                                                                        loopView.visible()
                                                                        loopView.setItems(districts)
                                                                    } else loopView.invisible()
                                                                }

                                                            })
                                                        }

                                                    })
                                        }

                                    })
                                }

                                .clicked(R.id.item_address_close) {
                                    listAddress.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        tvRight.setOneClickListener(View.OnClickListener { _ ->
            if (et_name.text.isEmpty()) {
                showToast("请输入姓名")
                return@OnClickListener
            }

            if (listTel.none { it.contact.isNotEmpty() }) {
                showToast("请添加联系电话")
                return@OnClickListener
            }

            if (listLicense.any { it.certificateNumber.isNotEmpty()
                            && it.certificateType.isEmpty() }) {
                showToast("请选择证件类型")
                return@OnClickListener
            }

            OkGo.post<String>(if (mTitle == "客户信息") BaseHttp.edit_usercustomer_sub else BaseHttp.add_usercustomer_sub)
                    .tag(this@ClientAddActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("customerName", et_name.text.toString())
                    .params("customerSex", customerSex)
                    .params("customerBirth", client_birth.text.toString())
                    .params("income", et_get.text.toString())
                    .params("height", et_height.text.toString())
                    .params("weight", et_weight.text.toString())
                    .params("cardNumber", cardNumber)
                    .params("cardBank", cardBank)
                    .params("cardMobile", cardMobile)
                    .params("memo", et_memo.text.trim().toString())
                    .apply {
                        if (mTitle == "客户信息") params("usercustomerId", usercustomerId)

                        if (listTel.isEmpty()) params("tels", "")
                        else {
                            val arr = JSONArray()
                            listTel.forEach {
                                if (it.contact.isNotEmpty()) {
                                    val inner = JSONObject()
                                    inner.put("contact", it.contact)
                                    arr.put(inner)
                                }
                            }
                            params("tels", arr.toString())
                        }

                        if (listEmail.isEmpty()) params("emails", "")
                        else {
                            val arr = JSONArray()
                            listEmail.forEach {
                                if (it.contact.isNotEmpty()) {
                                    val inner = JSONObject()
                                    inner.put("contact", it.contact)
                                    arr.put(inner)
                                }
                            }
                            params("emails", arr.toString())
                        }

                        if (listLicense.isEmpty()) params("certificates", "")
                        else {
                            val arr = JSONArray()
                            listLicense.forEach {
                                if (it.certificateNumber.isNotEmpty()) {
                                    val inner = JSONObject()
                                    inner.put("certificateType", it.certificateType)
                                    inner.put("certificateNumber", it.certificateNumber)
                                    inner.put("certificateStartDate", it.certificateStartDate)
                                    inner.put("certificateEndDate", it.certificateEndDate)
                                    inner.put("longValid", it.longValid)
                                    arr.put(inner)
                                }
                            }
                            params("certificates", arr.toString())
                        }

                        if (listAddress.isEmpty()) params("contactaddress", "")
                        else {
                            val arr = JSONArray()
                            listAddress.forEach {
                                if (it.province.isNotEmpty()
                                        || it.detailAdress.isNotEmpty()
                                        || it.postcode.isNotEmpty()) {
                                    val inner = JSONObject()
                                    inner.put("province", it.province)
                                    inner.put("city", it.city)
                                    inner.put("district", it.district)
                                    inner.put("detailAdress", it.detailAdress)
                                    inner.put("postcode", it.postcode)
                                    arr.put(inner)
                                }
                            }
                            params("contactaddress", arr.toString())
                        }
                    }
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            showToast(msg)
                            EventBus.getDefault().post(RefreshMessageEvent("更新客户"))
                            ActivityStack.screenManager.popActivities(this@ClientAddActivity::class.java)
                        }

                    })
        })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_tel -> {
                val index = listTel.size
                listTel.add(CommonData())
                (client_tel_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_email -> {
                val index = listEmail.size
                listEmail.add(CommonData())
                (client_email_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_license -> {
                val index = listLicense.size
                listLicense.add(CommonData())
                (client_license_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_addr -> {
                val index = listAddress.size
                listAddress.add(CommonData())
                (client_addr_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_gender_ll -> {
                val items = listOf("男", "女")
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择性别",
                        items) { position, name ->

                    customerSex = 1 - position
                    client_gender.text = name
                }
            }
            R.id.client_birth_ll -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - 100,
                        year,
                        3,
                        "选择出生日期",
                        true,
                        false,
                        true) { _, _, _, _, _, date ->

                    client_birth.text = date
                }
            }
            R.id.client_bank_ll -> startActivity<ClientBankActivity>(
                    "number" to cardNumber,
                    "bank" to cardBank,
                    "phone" to cardMobile)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.find_usercustome_details)
                .tag(this@ClientAddActivity)
                .headers("token", getString("token"))
                .params("usercustomerId", usercustomerId)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        val data = response.body().`object` ?: return

                        val item = data.u
                        customerSex = if (item.customerSex.isEmpty()) 1 else item.customerSex.toInt()
                        cardNumber = item.cardNumber
                        cardBank = item.cardBank
                        cardMobile = item.cardMobile

                        et_name.setText(item.customerName)
                        client_gender.text = if (customerSex == 0) "女" else "男"
                        client_birth.text = item.customerBirth
                        et_get.setText(item.income)
                        et_height.setText(item.height)
                        et_weight.setText(item.weight)
                        client_bank.text = cardNumber.bankCardReplaceHeaderWithStar()
                        et_memo.setText(item.memo)

                        listTel.clear()
                        listEmail.clear()
                        listLicense.clear()
                        listAddress.clear()
                        listTel.addItems(data.tels)
                        listEmail.addItems(data.emails)
                        listLicense.addItems(data.certificates)
                        listAddress.addItems(data.contactaddress)

                        (client_tel_list.adapter as SlimAdapter).notifyDataSetChanged()
                        (client_email_list.adapter as SlimAdapter).notifyDataSetChanged()
                        (client_license_list.adapter as SlimAdapter).notifyDataSetChanged()
                        (client_addr_list.adapter as SlimAdapter).notifyDataSetChanged()
                    }

                })
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.city_name_data)
                .tag(this@ClientAddActivity)
                .params("areaLevel", "country")
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        listProvince.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                    }

                    override fun onFinish() {
                        if (listProvince.isNotEmpty()) getCity(listProvince[0].areaName, callback)
                    }

                })
    }

    private fun getCity(areaName: String, callback: ResultCallBack) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.city_name_data)
                .tag(this@ClientAddActivity)
                .isMultipart(true)
                .params("areaLevel", "province")
                .params("areaName", areaName)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        listCity.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                    }

                    override fun onFinish() {
                        if (listCity.isNotEmpty()) getDistrict(listCity[0].areaName, callback)
                    }

                })
    }

    private fun getDistrict(areaName: String, callback: ResultCallBack) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.city_name_data)
                .tag(this@ClientAddActivity)
                .isMultipart(true)
                .params("areaLevel", "city")
                .params("areaName", areaName)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        listDistrict.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                    }

                    override fun onFinish() {
                        if (listDistrict.isNotEmpty()) callback.doWork()
                    }

                })
    }

    interface ResultCallBack {
        fun doWork()
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ClientAddActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: BankMessageEvent) {
        cardNumber = event.cardNo
        cardBank = event.cardBank
        cardMobile = event.cardPhone

        client_bank.text = cardNumber.bankCardReplaceHeaderWithStar()
    }
}
