package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.visible

class PlanLookActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_look)
        init_title("平安福2018")
    }

    override fun init_title() {
        super.init_title()
        ivRight.visible()
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.iv_nav_right -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_share_bottom, null) as View
                val wechat = view.findViewById<LinearLayout>(R.id.dialog_share_wechat)
                val circle = view.findViewById<LinearLayout>(R.id.dialog_share_circle)
                val qq = view.findViewById<LinearLayout>(R.id.dialog_share_qq)
                val space = view.findViewById<LinearLayout>(R.id.dialog_share_space)
                val cancel = view.findViewById<Button>(R.id.dialog_share_cancel)
                val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

                wechat.setOnClickListener {
                    dialog.dismiss()}
                circle.setOnClickListener {
                    dialog.dismiss()
                }
                qq.setOnClickListener {
                    dialog.dismiss()
                }
                space.setOnClickListener {
                    dialog.dismiss()
                }
                cancel.setOnClickListener { dialog.dismiss() }

                dialog.setContentView(view)
                dialog.show()
            }
        }
    }
}
