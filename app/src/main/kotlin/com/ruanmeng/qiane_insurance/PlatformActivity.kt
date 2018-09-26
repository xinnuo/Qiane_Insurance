package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.startActivity

class PlatformActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        init_title("平台资质")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.platform_photo -> startActivity<WebActivity>("title" to "公司相关拍照和证件")
            R.id.platform_list ->  startActivity<WebActivity>("title" to "授权合作单位列表")
        }
    }
}
