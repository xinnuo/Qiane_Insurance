package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.optStringNotEmpty
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_income.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class IncomeActivity : BaseActivity() {

    private var mBalance = "0.00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)
        init_title("我的收入", "明细")

        EventBus.getDefault().register(this@IncomeActivity)

        getData()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.income_rule -> startActivity<WebActivity>("title" to "提现规则")
            R.id.tv_nav_right -> startActivity<IncomeDetailActivity>()
            R.id.bt_withdraw -> startActivity<IncomeWithdrawActivity>("balance" to mBalance)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.assets_info)
                .tag(this@IncomeActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()

                        mBalance = String.format("%.2f", obj.optStringNotEmpty("balance", "0.00").toDouble())
                        val profitSum = obj.optStringNotEmpty("profitSum", "0.00").toDouble()

                        income_total.text = "￥${String.format("%.2f", profitSum)}"
                        income_withdraw.text = "￥$mBalance"
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@IncomeActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "提现" -> getData()
        }
    }
}
