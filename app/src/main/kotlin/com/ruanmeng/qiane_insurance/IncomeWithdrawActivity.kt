package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.DecimalNumberFilter
import kotlinx.android.synthetic.main.activity_income_withdraw.*
import java.text.DecimalFormat

class IncomeWithdrawActivity : BaseActivity() {

    private var withdrawSum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_withdraw)
        init_title("提现")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        withdrawSum = intent.getStringExtra("balance").toDouble()
        withdraw_money.text = "可提现金额${DecimalFormat(",##0.00").format(withdrawSum)}元"

        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_submit.isClickable = false

        et_count.filters = arrayOf<InputFilter>(DecimalNumberFilter())
        withdraw_type.addTextChangedListener(this@IncomeWithdrawActivity)
        et_count.addTextChangedListener(this@IncomeWithdrawActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.withdraw_type_ll -> {}
            R.id.bt_submit -> {}
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (withdraw_type.text.isNotBlank()
                && et_count.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_red)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}
