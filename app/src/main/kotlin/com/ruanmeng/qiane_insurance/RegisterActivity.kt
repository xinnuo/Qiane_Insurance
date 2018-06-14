package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

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

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_login -> ActivityStack.screenManager.popActivities(this::class.java)
            R.id.register_deal -> startActivity<WebActivity>("title" to "注册协议")
            R.id.bt_yzm -> {}
            R.id.bt_register -> { startActivity<RegisterDoneActivity>() }
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
