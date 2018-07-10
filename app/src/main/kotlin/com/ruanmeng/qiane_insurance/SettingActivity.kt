package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.luck.picture.lib.tools.PictureFileUtils
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getBoolean
import com.ruanmeng.base.getString
import com.ruanmeng.base.putBoolean
import com.ruanmeng.share.Const
import com.ruanmeng.utils.GlideCacheUtil
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        setting_cache.text = GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity)
        setting_version.text = "v${Tools.getVersion(baseContext)}"
        setting_switch.isChecked = !getBoolean("isTS")

        setting_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                JPushInterface.resumePush(applicationContext)
                JPushInterface.setAlias(
                        applicationContext,
                        Const.JPUSH_SEQUENCE,
                        getString("token"))
                putBoolean("isTS", false)
            } else {
                JPushInterface.stopPush(applicationContext)
                putBoolean("isTS", true)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.setting_password -> startActivity<PasswordActivity>()
            R.id.setting_about -> startActivity<WebActivity>("title" to "关于我们")
            R.id.setting_feedback -> startActivity<FeedbackActivity>()
            R.id.setting_cache_ll -> {
                alert {
                    title = "清空缓存"
                    message = "确定要清空缓存吗？"
                    negativeButton("取消") {}
                    positiveButton("清空") {
                        GlideCacheUtil.getInstance().clearImageAllCache(baseContext)
                        PictureFileUtils.deleteCacheDirFile(baseContext)
                        setting_cache.text = "0B"
                    }
                }.show()
            }
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
