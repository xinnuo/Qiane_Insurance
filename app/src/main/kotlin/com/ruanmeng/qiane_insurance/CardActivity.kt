package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_card.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class CardActivity : BaseActivity() {

    private var mTelephone = ""
    private var mWechat = ""
    private var mRealName = ""
    private var mCompany = ""
    private var mUserHead = ""
    private var mCompanyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        init_title("我的名片")

        getData()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.card_introduce -> {
                if (mCompanyId.isEmpty()) return
                startActivity<WebActivity>(
                        "title" to "公司介绍",
                        "companyId" to mCompanyId)
            }
            R.id.card_edit -> startActivity<CardEditActivity>(
                    "head" to mUserHead,
                    "name" to mRealName,
                    "company" to mCompany)
            R.id.card_trans -> {

            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_business_card)
                .tag(this@CardActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()

                        mCompanyId = obj.optString("companyId")
                        mTelephone = obj.optString("telephone")
                        mWechat = obj.optString("wechat")
                        mUserHead = obj.optString("userhead")
                        mRealName = obj.optString("realName")
                        mCompany = obj.optString("companyName")

                        card_img.loadImage(BaseHttp.baseImg + mUserHead)
                        card_name.text = mRealName
                        card_company.text = mCompany
                        card_get.text = obj.optStringNotEmpty("praiseSum", "0")
                        card_planed.text = obj.optStringNotEmpty("prospectusSum", "0")
                    }

                })
    }
}
