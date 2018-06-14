package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack

class CardEditActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_edit)
        init_title("编辑名片")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.edit_img_ll -> {}
            R.id.edit_company_ll -> startActivity<CompanyActivity>()
            R.id.edit_wechat_ll -> {}
            R.id.bt_save -> {
                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }
}
