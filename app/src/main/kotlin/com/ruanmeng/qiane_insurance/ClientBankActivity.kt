package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_client_bank.*

class ClientBankActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_bank)
        init_title("添加银行卡")
    }

    override fun init_title() {
        super.init_title()
        bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_save.isClickable = false

        et_card.addTextChangedListener(this)
        client_bank.addTextChangedListener(this)
        et_tel.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_bank_ll -> {
                val items = listOf("中国工商银行", "中国建设银行", "中国农业银行")
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择开户行",
                        items) { _, name ->
                    client_bank.text = name
                }
            }
            R.id.bt_save -> {
                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_card.text.isNotBlank()
                && client_bank.text.isNotBlank()
                && et_tel.text.isNotBlank()) {
            bt_save.setBackgroundResource(R.drawable.rec_bg_red)
            bt_save.isClickable = true
        } else {
            bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_save.isClickable = false
        }
    }
}
