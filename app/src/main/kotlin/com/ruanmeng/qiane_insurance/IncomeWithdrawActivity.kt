package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class IncomeWithdrawActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_withdraw)
        init_title("提现")
    }
}
