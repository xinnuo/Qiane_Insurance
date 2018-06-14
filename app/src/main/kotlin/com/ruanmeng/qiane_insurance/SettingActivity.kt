package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import org.jetbrains.anko.alert

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.setting_password -> startActivity<PasswordActivity>()
            R.id.setting_about -> startActivity<WebActivity>("title" to "关于我们")
            R.id.setting_feedback -> {}
            R.id.setting_cache_ll -> {}
            R.id.setting_version_ll -> {}
            R.id.bt_quit -> {
                /*AlertDialog.newBuilder(baseContext)
                        .setTitle("退出登录")
                        .setMessage("确定要退出当前账号吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("退出", { _, _ ->
                            startActivity<LoginActivity>("offLine" to true)
                        })
                        .show()*/
                alert {
                    title = "退出登录"
                    message = "确定要退出当前账号吗？"
                    negativeButton("取消") {}
                    positiveButton("退出") {
                        startActivity<LoginActivity>("offLine" to true)
                    }
                }.show()
            }
        }
    }
}
