package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        init_title("个人资料")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.info_gender_ll -> {
                val items = listOf("男", "女")
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择性别",
                        items) { _, name ->
                    info_gender.text = name
                }
            }
            R.id.info_tel_ll -> startActivity<InfoPhoneActivity>()
            R.id.info_real_ll -> startActivity<InfoRealActivity>("title" to "实名认证", "hint" to "确认")
            R.id.info_job -> startActivity<InfoJobActivity>()
        }
    }
}
