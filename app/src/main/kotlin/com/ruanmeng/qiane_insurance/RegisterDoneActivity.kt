package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import org.jetbrains.anko.startActivity

class RegisterDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_done)
        init_title("注册")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_no -> ActivityStack.screenManager.popActivities(this::class.java)
            R.id.register_yes -> {
                startActivity<InfoRealActivity>(
                        "title" to "实名认证",
                        "hint" to "确认",
                        "token" to intent.getStringExtra("token"))
                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }
}
