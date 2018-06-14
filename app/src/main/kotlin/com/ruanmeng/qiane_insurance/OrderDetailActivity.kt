package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class OrderDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        init_title("订单详情")
    }
}
