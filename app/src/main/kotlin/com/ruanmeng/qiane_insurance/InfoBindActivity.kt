package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_info_bind.*

class InfoBindActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_bind)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()

        when (intent.getStringExtra("type")) {
            "1" -> {
                bind_number.hint = "请输入银行卡卡号"
                bind_number.setText(getString("bankNo"))
                bind_number.inputType = InputType.TYPE_CLASS_NUMBER
                bind_number.filters = arrayOf<InputFilter>(NameLengthFilter(19))
            }
            "2" -> {
                bind_number.hint = "请输入支付宝账号"
                bind_number.setText(getString("alipay"))
                bind_number.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                bind_number.filters = arrayOf<InputFilter>(NameLengthFilter(30))
            }
        }

        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        bind_number.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_ok -> {
                val inputText = bind_number.text.toString()

                when (intent.getStringExtra("type")) {
                    "1" -> {
                        if (!BankcardHelper.checkBankCard(inputText)) {
                            showToast("请输入正确的银行卡卡号")
                            return
                        }

                        OkGo.post<String>(BaseHttp.edit_userbankNo)
                                .tag(this@InfoBindActivity)
                                .headers("token", getString("token"))
                                .params("bankNo", bind_number.text.toString())
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                        showToast(msg)
                                        putString("bankNo", bind_number.text.toString())
                                        ActivityStack.screenManager.popActivities(this@InfoBindActivity::class.java)
                                    }

                                })
                    }
                    "2" -> {
                        if (!inputText.isEmail() && !inputText.isMobile()) {
                            showToast("请输入正确的支付宝账号")
                            return
                        }

                        OkGo.post<String>(BaseHttp.edit_useralipay)
                                .tag(this@InfoBindActivity)
                                .headers("token", getString("token"))
                                .params("alipay", bind_number.text.toString())
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                        showToast(msg)
                                        putString("alipay", bind_number.text.toString())
                                        ActivityStack.screenManager.popActivities(this@InfoBindActivity::class.java)
                                    }

                                })
                    }
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (bind_number.text.isNotBlank()
                && bind_number.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_red)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
