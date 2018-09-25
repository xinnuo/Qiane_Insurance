package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_income_withdraw.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat

class IncomeWithdrawActivity : BaseActivity() {

    private var withdrawSum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_withdraw)
        init_title("提现", "记录")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        withdrawSum = intent.getStringExtra("balance").toDouble()
        withdraw_money.text = "可提现金额${DecimalFormat(",##0.00").format(withdrawSum)}元"

        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_submit.isClickable = false

        et_count.filters = arrayOf<InputFilter>(DecimalNumberFilter())
        withdraw_type.addTextChangedListener(this@IncomeWithdrawActivity)
        et_num.addTextChangedListener(this@IncomeWithdrawActivity)
        et_count.addTextChangedListener(this@IncomeWithdrawActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<IncomeWithdrawListActivity>()
            R.id.withdraw_type_ll -> {
                val items = listOf("银行卡", "支付宝")
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择年龄",
                        mPosition,
                        items) { position, name ->
                    withdraw_type.text = name
                    mPosition = position
                    withdraw_expand.expand()

                    when (position) {
                        0 -> {
                            withdraw_hint.text = "银行卡卡号"
                            et_num.hint = "请输入银行卡卡号"
                            et_num.setText("")
                            et_num.inputType = InputType.TYPE_CLASS_NUMBER
                            et_num.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(19))
                        }
                        1 -> {
                            withdraw_hint.text = "支付宝账号"
                            et_num.hint = "请输入支付宝账号"
                            et_num.setText("")
                            et_num.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            et_num.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                        }
                    }
                }
            }
            R.id.bt_submit -> {
                when (mPosition) {
                    0 -> {
                        if (!BankcardHelper.checkBankCard(et_num.text.toString())) {
                            showToast("请输入正确的银行卡卡号")
                            return
                        }
                    }
                    1 -> {
                        if (!et_num.text.toString().isMobile() && !et_num.text.toString().isEmail()) {
                            showToast("请输入正确的支付宝账号")
                            return
                        }
                    }
                }

                val inputCount = et_count.text.toString().toDouble()

                if (inputCount < 1) {
                    showToast("最低提现金额不少于1元")
                    return
                }

                if (inputCount > withdrawSum) {
                    showToast("提现金额不足")
                    return
                }

                OkGo.post<String>(BaseHttp.add_withdraw_sub)
                        .tag(this@IncomeWithdrawActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("withdrawSum", et_count.text.toString())
                        .params("carno", et_num.text.toString())
                        .params("withdrawWay", withdraw_type.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                EventBus.getDefault().post(RefreshMessageEvent("提现"))
                                ActivityStack.screenManager.popActivities(this@IncomeWithdrawActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (withdraw_type.text.isNotBlank()
                && et_num.text.isNotBlank()
                && et_count.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_red)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}
