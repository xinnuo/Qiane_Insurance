package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.putBoolean
import com.ruanmeng.base.putString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class RegisterDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_done)
        init_title("注册")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_no -> {
                OkGo.post<String>(BaseHttp.login_sub)
                        .tag(this@RegisterDoneActivity)
                        .params("accountName", intent.getStringExtra("account"))
                        .params("password", intent.getStringExtra("password"))
                        .params("loginType", "mobile")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body()).optJSONObject("object")
                                        ?: JSONObject()

                                putBoolean("isLogin", true)
                                putString("token", obj.optString("token"))
                                putString("mobile", obj.optString("mobile"))
                                putString("companyId", obj.optString("companyId"))
                                putString("companyName", obj.optString("companyName"))

                                startActivity<MainActivity>()
                                ActivityStack.screenManager.popActivities(this@RegisterDoneActivity::class.java, LoginActivity::class.java)
                            }

                        })
            }
            R.id.register_yes -> {
                startActivity<InfoRealActivity>(
                        "title" to "实名认证",
                        "hint" to "确认",
                        "account" to intent.getStringExtra("account"),
                        "password" to intent.getStringExtra("password"),
                        "token" to intent.getStringExtra("token"))
                ActivityStack.screenManager.popActivities(this::class.java)
            }
        }
    }
}
