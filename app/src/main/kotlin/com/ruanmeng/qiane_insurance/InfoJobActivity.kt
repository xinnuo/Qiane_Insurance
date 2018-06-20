package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_info_job.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class InfoJobActivity : BaseActivity() {

    private var companyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_job)
        init_title("职业信息")

        EventBus.getDefault().register(this@InfoJobActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        job_company.addTextChangedListener(this)
        job_number.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.job_clear -> job_number.setText("")
            R.id.job_company_ll -> startActivity<CompanyActivity>("type" to "职业信息")
            R.id.bt_ok -> {
                OkGo.post<String>(BaseHttp.add_profession_info)
                        .tag(this@InfoJobActivity)
                        .headers("token", getString("token"))
                        .params("companyId", companyId)
                        .params("workNumber", job_number.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@InfoJobActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_profession_info)
                .tag(this@InfoJobActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()

                        job_company.text = obj.optString("companyName")
                        job_number.setText(obj.optString("workNumber"))
                        job_number.setSelection(job_number.text.length)
                    }

                })
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (job_company.text.isNotBlank()
                && job_number.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_red)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@InfoJobActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "职业信息" -> {
                companyId = event.id
                job_company.text = event.name
            }
        }
    }
}
