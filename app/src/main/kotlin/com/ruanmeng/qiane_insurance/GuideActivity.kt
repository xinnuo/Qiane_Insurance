package com.ruanmeng.qiane_insurance

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.WindowManager
import android.widget.ImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getBoolean
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.*

class GuideActivity : BaseActivity() {

    private var isReady: Boolean = false

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isReady) quitGuide()
            else isReady = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏（全屏）
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        transparentStatusBar(false)

        frameLayout {
            imageView {
                backgroundResource = R.drawable.guide
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(width = matchParent, height = matchParent)
        }

        window.decorView.postDelayed({ handler.sendEmptyMessage(0) }, 2000)

        AndPermission.with(this@GuideActivity)
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                        handler.sendEmptyMessage(0)
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                        showToast("请求权限被拒绝")
                        onBackPressed()
                    }
                }).start()
    }

    private fun quitGuide() {
        if (getBoolean("isLogin")) startActivity<MainActivity>()
        else startActivity<LoginActivity>()
        ActivityStack.screenManager.popActivities(this@GuideActivity::class.java)
    }
}
