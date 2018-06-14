package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_info_job.*

class InfoJobActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_job)
        init_title("职业信息")
    }

    override fun init_title() {
        super.init_title()
        job_company.text = "中国平安"
        job_number.setText("230089")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.job_clear -> job_number.setText("")
            R.id.job_company_ll -> startActivity<CompanyActivity>()
        }
    }
}
