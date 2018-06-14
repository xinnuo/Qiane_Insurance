package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        init_title("修改登录密码")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_tel.addTextChangedListener(this)
        et_yzm.addTextChangedListener(this)
        et_new.addTextChangedListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_yzm -> {
                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!CommonUtil.isMobile(et_tel.text.toString())) {
                    et_tel.requestFocus()
                    et_tel.setText("")
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
            }
            R.id.bt_ok -> {
                if (!CommonUtil.isMobile(et_tel.text.toString())) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                if (et_tel.text.toString() != mTel) {
                    showToast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    showToast("验证码错误，请重新输入")
                    return
                }

                if (et_new.text.length < 6) {
                    showToast("新密码长度不少于6位")
                    return
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_tel.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_new.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_red)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
