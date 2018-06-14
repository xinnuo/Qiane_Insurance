package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_info_real.*

class InfoRealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_real)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        bt_ok.text = intent.getStringExtra("hint")
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_name.addTextChangedListener(this)
        et_card.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_ok -> ActivityStack.screenManager.popActivities(this::class.java)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_card.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_red)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
