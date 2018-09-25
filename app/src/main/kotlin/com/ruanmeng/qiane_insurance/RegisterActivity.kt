package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DESUtil
import com.ruanmeng.utils.EncryptUtil
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class RegisterActivity : BaseActivity() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        transparentStatusBar(false)
        init_title()
    }

    override fun init_title() {
        bt_register.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_register.isClickable = false

        et_name.addTextChangedListener(this)
        et_yzm.addTextChangedListener(this)
        et_pwd.addTextChangedListener(this)
        cb_pwd.setOnCheckedChangeListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_login -> ActivityStack.screenManager.popActivities(this::class.java)
            R.id.register_deal -> startActivity<WebActivity>("title" to "注册协议")
            R.id.bt_yzm -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!et_name.text.toString().isMobile()) {
                    et_name.requestFocus()
                    et_name.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                thread = Runnable {
                    bt_yzm.text = "${time_count}秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "获取验证码"
                        bt_yzm.isClickable = true
                        time_count = 180
                    }
                }

                EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
                val encodeTel = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), et_name.text.toString())

                OkGo.post<String>(BaseHttp.identify_get)
                        .tag(this@RegisterActivity)
                        .isMultipart(true)
                        .params("mobile", encodeTel)
                        .params("time", Const.MAKER)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).optString("object")
                                mTel = et_name.text.toString()
                                if (BuildConfig.LOG_DEBUG) {
                                    et_yzm.setText(YZM)
                                    et_yzm.setSelection(et_yzm.text.length)
                                }

                                bt_yzm.isClickable = false
                                time_count = 180
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.bt_register -> {
                if (!et_name.text.toString().isMobile()) {
                    et_name.requestFocus()
                    et_name.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                if (et_name.text.toString() != mTel) {
                    showToast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    showToast("验证码错误，请重新输入")
                    return
                }

                if (et_pwd.text.length < 6) {
                    showToast("新密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.register_sub)
                        .tag(this@RegisterActivity)
                        .params("mobile", mTel)
                        .params("loginType", "mobile")
                        .params("smscode", et_yzm.text.toString())
                        .params("password", et_pwd.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()
                                val token = obj.optString("token")
                                startActivity<RegisterDoneActivity>(
                                        "token" to token,
                                        "account" to mTel,
                                        "password" to et_pwd.text.toString())
                                ActivityStack.screenManager.popActivities(this@RegisterActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_register.setBackgroundResource(R.drawable.rec_bg_red)
            bt_register.isClickable = true
        } else {
            bt_register.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_register.isClickable = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        } else {
            et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        }
    }
}
