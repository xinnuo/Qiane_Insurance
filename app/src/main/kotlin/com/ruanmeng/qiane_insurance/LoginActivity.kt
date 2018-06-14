package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putBoolean
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        transparentStatusBar(false)
        init_title()
    }

    override fun init_title() {
        bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_login.isClickable = false

        et_name.addTextChangedListener(this)
        et_pwd.addTextChangedListener(this)
        cb_pwd.setOnCheckedChangeListener(this)

        if (getString("mobile").isNotEmpty()) {
            et_name.setText(getString("mobile"))
            et_name.setSelection(et_name.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            clearData()
            ActivityStack.screenManager.popAllActivityExcept(LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.login_sign -> startActivity<RegisterActivity>()
            R.id.login_forget -> startActivity<ForgetActivity>()
            R.id.login_deal -> startActivity<WebActivity>("title" to "注册协议")
            R.id.bt_login -> {
                putBoolean("isLogin", true)
                startActivity<MainActivity>()
                ActivityStack.screenManager.popActivities(this@LoginActivity::class.java)
            }
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_login.setBackgroundResource(R.drawable.rec_bg_red)
            bt_login.isClickable = true
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_login.isClickable = false
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
