package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.startActivity

class ContactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        init_title("联系我们")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.contact_tel ->     startActivity<WebActivity>("title" to "合作电话")
            R.id.contact_email ->   startActivity<WebActivity>("title" to "合作邮箱")
            R.id.contact_tel2 ->    startActivity<WebActivity>("title" to "客服电话")
            R.id.contact_qq ->      startActivity<WebActivity>("title" to "客服QQ")
            R.id.contact_email2 ->  startActivity<WebActivity>("title" to "客服邮箱")
            R.id.contact_suggest -> startActivity<WebActivity>("title" to "投诉建议电话")
        }
    }
}
