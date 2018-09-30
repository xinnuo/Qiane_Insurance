package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.makeCall
import com.ruanmeng.share.BaseHttp
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class ContactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        init_title("联系我们")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.contact_tel ->     getTelData("hzdh")
            R.id.contact_email ->   startActivity<WebActivity>("title" to "合作邮箱")
            R.id.contact_tel2 ->    getTelData("kfdh")
            R.id.contact_qq ->      startActivity<WebActivity>("title" to "客服QQ")
            R.id.contact_email2 ->  startActivity<WebActivity>("title" to "客服邮箱")
            R.id.contact_suggest -> getTelData("tsjydh")
        }
    }

    private fun getTelData(value: String) {
        OkGo.post<String>(BaseHttp.help_center)
                .tag(this@ContactActivity)
                .params("htmlKey", value)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val mTel = JSONObject(response.body()).optString("help")
                        alert {
                            title = "拨打电话"
                            message = when (value) {
                                "hzdh" -> "合作电话：$mTel"
                                "kfdh" -> "客服电话：$mTel"
                                "tsjydh" -> "投诉建议电话：$mTel"
                                else -> mTel
                            }
                            negativeButton("取消") {}
                            positiveButton("拨打") {
                                if (mTel.isNotEmpty()) makeCall(mTel)
                            }
                        }.show()
                    }

                })
    }
}
