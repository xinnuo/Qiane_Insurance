package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class PlanSendActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_send)
        init_title("投保页")
    }
}
