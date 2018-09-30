package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.Gravity
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick

class PointListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            themedTextView("分享好友获取积分", R.style.Font14_black) {
                gravity = Gravity.CENTER_VERTICAL
                backgroundColorResource = R.color.white
                setPadding(dip(10), dip(0), dip(10), dip(0))
                onClick { startActivity<ShareActivity>() }
            }.lparams(width = matchParent, height = dip(45))

            view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))
        }
        init_title("积分任务")
    }
}
