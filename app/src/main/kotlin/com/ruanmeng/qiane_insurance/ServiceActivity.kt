package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.startActivity

class ServiceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        init_title("客服")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.service_commission -> startActivity<WebActivity>("title" to "佣金结算问题")
            R.id.service_member ->     startActivity<WebActivity>("title" to "会员活动问题")
            R.id.service_common ->     startActivity<WebActivity>("title" to "常见问题")
            R.id.service_use ->        startActivity<WebActivity>("title" to "APP使用问题")
        }
    }
}
