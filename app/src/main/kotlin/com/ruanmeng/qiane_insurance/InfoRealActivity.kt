package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
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

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))
        et_name.addTextChangedListener(this)
        et_card.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_ok -> {
                if (!CommonUtil.IDCardValidate(et_card.text.toString())) {
                    showToast("请输入正确的身份证号")
                    et_card.requestFocus()
                    et_card.setText("")
                    return
                }

                OkGo.post<String>(BaseHttp.certification_sub)
                        .tag(this@InfoRealActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("userName", et_name.text.trim().toString())
                        .params("cardNo", et_card.text.toString().toUpperCase())
                        .params("type", when (intent.getStringExtra("title")) {
                            "实名认证" -> "0"
                            "获取资质证书" -> "1"
                            else -> ""
                        })
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                if (intent.getStringExtra("title") == "实名认证") {
                                    putString("realName", et_name.text.trim().toString())
                                    putString("pass", "-1")
                                }
                                ActivityStack.screenManager.popActivities(this@InfoRealActivity::class.java)
                            }

                        })
            }
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
