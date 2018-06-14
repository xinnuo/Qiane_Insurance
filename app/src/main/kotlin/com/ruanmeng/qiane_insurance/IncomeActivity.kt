package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity

class IncomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)
        init_title("我的收入", "明细")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.income_rule -> startActivity<WebActivity>("title" to "提现规则")
            R.id.tv_nav_right -> startActivity<IncomeDetailActivity>()
            R.id.bt_withdraw -> startActivity<IncomeWithdrawActivity>()
        }
    }
}
