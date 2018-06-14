package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.Gravity
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick

class PlanDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init_title("平安福2018")

        frameLayout {
            backgroundResource = R.mipmap.plan_bj

            verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL

                themedTextView("亲启", R.style.Font15_white) {
                    backgroundResource = R.drawable.oval_ab5751
                    textSizeDimen = R.dimen.sp_size_18
                    gravity = Gravity.CENTER
                    onClick { startActivity<PlanLookActivity>() }
                }.lparams(width = dip(65), height = dip(65))

                themedTextView("敬呈", R.style.Font15_white) {
                    textColorResource = R.color.brown
                    textSizeDimen = R.dimen.sp_size_18
                    gravity = Gravity.CENTER
                }.lparams { topMargin = dip(15) }

            }.lparams {
                gravity = Gravity.CENTER
                topMargin = dip(100)
            }
        }
    }
}
