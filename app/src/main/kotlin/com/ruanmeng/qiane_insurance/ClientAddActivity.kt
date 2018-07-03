package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NameLengthFilter
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_client_add.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.startActivity
import java.util.*

class ClientAddActivity : BaseActivity() {

    private val listTel = ArrayList<CommonData>()
    private val listEmail = ArrayList<CommonData>()
    private val listLicense = ArrayList<CommonData>()
    private val listAddress = ArrayList<CommonData>()
    private var listProvince = ArrayList<CommonData>()
    private var listCity = ArrayList<CommonData>()
    private var listDistrict = ArrayList<CommonData>()
    private var customerSex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_add)
        init_title("手动添加", "完成")

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
            items.forEach { listTel.add(CommonData().apply { title = it }) }
        }
        if (userEmail.isNotEmpty()) {
            val items = userEmail.split(",")
            items.forEach { listEmail.add(CommonData().apply { title = it }) }
        }

        (client_tel_list.adapter as SlimAdapter).updateData(listTel)
        (client_email_list.adapter as SlimAdapter).updateData(listEmail)
        (client_license_list.adapter as SlimAdapter).updateData(listLicense)
        (client_addr_list.adapter as SlimAdapter).updateData(listAddress)
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(16))

        client_tel_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_tel_list) { data, injector ->
                        injector.text(R.id.item_tel_input, data.title)

                                .with<EditText>(R.id.item_tel_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.title)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.title = s.toString()
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
                        injector.text(R.id.item_email_input, data.title)

                                .with<EditText>(R.id.item_email_input) {

                                    if (it.tag != null && it.tag is TextWatcher) {
                                        it.removeTextChangedListener(it.tag as TextWatcher)
                                    }

                                    it.setText(data.title)
                                    it.setSelection(it.text.length)

                                    val textWatcher = object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {
                                            data.title = s.toString()
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
                        injector.text(R.id.item_address_input, "")
                                .clicked(R.id.item_address_close) {
                                    val index = listAddress.indexOf(data)
                                    listAddress.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_tel -> {
                val index = listTel.size
                listTel.add(CommonData("$index"))
                (client_tel_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_email -> {
                val index = listEmail.size
                listEmail.add(CommonData("$index"))
                (client_email_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_license -> {
                val index = listLicense.size
                listLicense.add(CommonData("$index"))
                (client_license_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_addr -> {
                val index = listAddress.size
                listAddress.add(CommonData("$index"))
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
                        true) { _, _, _, _, _, date ->

                    client_birth.text = date
                }
            }
            R.id.client_bank_ll -> startActivity<ClientBankActivity>()
        }
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.city_name_data)
                .tag(this@ClientAddActivity)
                .params("areaLevel", "country")
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

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
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

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
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

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
}
