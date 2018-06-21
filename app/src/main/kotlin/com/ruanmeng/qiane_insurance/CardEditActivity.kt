package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.loadImage
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_card_edit.*
import org.jetbrains.anko.startActivity

class CardEditActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_edit)
        init_title("编辑名片")
    }

    override fun init_title() {
        super.init_title()
        loadUserHead(intent.getStringExtra("head"))
        edit_name.text = intent.getStringExtra("name")
        edit_company.text = intent.getStringExtra("company")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.edit_img_ll -> {}
            R.id.edit_company_ll -> startActivity<CompanyActivity>("type" to "编辑名片")
            R.id.edit_wechat_ll -> {}
            R.id.bt_save -> {
                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }

    private fun loadUserHead(path: String) = edit_img.loadImage(BaseHttp.baseImg + path)
}
