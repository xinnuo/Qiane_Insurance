package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.model.BankMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.BankcardHelper
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.activity_client_bank.*
import org.greenrobot.eventbus.EventBus

class ClientBankActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_bank)
        init_title("添加银行卡")
    }

    override fun init_title() {
        super.init_title()
        bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_save.isClickable = false

        et_card.addTextChangedListener(this)
        client_bank.addTextChangedListener(this)
        et_tel.addTextChangedListener(this)

        et_card.setText(intent.getStringExtra("number"))
        client_bank.text = intent.getStringExtra("bank")
        et_tel.setText(intent.getStringExtra("phone"))
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_bank_ll -> {
                val items = resources.getStringArray(R.array.bankName).asList()
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择开户行",
                        items) { _, name ->
                    client_bank.text = name
                }
            }
            R.id.bt_save -> {
                if (!BankcardHelper.checkBankCard(et_card.rawText)) {
                    showToast("请输入正确的银行卡卡号")
                    return
                }

                if (!et_tel.text.toString().isMobile()) {
                    showToast("请输入正确的手机号码")
                    return
                }

                EventBus.getDefault().post(BankMessageEvent(
                        et_card.rawText,
                        client_bank.text.toString(),
                        et_tel.text.toString()))

                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_card.text!!.isNotBlank()
                && client_bank.text.isNotBlank()
                && et_tel.text.isNotBlank()) {
            bt_save.setBackgroundResource(R.drawable.rec_bg_red)
            bt_save.isClickable = true
        } else {
            bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_save.isClickable = false
        }
    }
}
