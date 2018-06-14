package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity

class CardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        init_title("我的名片")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.card_edit -> startActivity<CardEditActivity>()
            R.id.card_trans -> {

            }
        }
    }
}
