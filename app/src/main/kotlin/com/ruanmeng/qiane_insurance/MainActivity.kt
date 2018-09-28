package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transparentStatusBar(false)
        init_title()

        if (!getBoolean("isTS")) {
            JPushInterface.resumePush(applicationContext)
            //设置别名（先初始化）
            JPushInterface.setAlias(
                    applicationContext,
                    Const.JPUSH_SEQUENCE,
                    getString("token"))
        }

        main_check1.performClick()
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            R.id.main_check3 -> MainThirdFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 3
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.first_plan -> startActivity<PlanActivity>()
            R.id.first_tool -> startActivity<ToolActivity>()
            R.id.first_company -> startActivity<CompanyActivity>("type" to "选择公司")
            R.id.second_read_ll -> startActivity<ReadActivity>()
            R.id.first_search, R.id.second_search -> startActivity<SearchActivity>()
            R.id.first_message, R.id.third_message -> startActivity<MessageActivity>()
            R.id.first_share, R.id.third_share -> startActivity<ShareActivity>()
            R.id.second_total_ll, R.id.third_guest -> startActivity<ClientActivity>()

            R.id.third_setting -> startActivity<SettingActivity>()
            R.id.third_info -> startActivity<InfoActivity>()
            R.id.third_card -> startActivity<PlanLookActivity>("type" to "我的名片")
            R.id.third_income_ll -> startActivity<IncomeActivity>()
            R.id.third_point_ll -> startActivity<PointActivity>()
            R.id.third_order, R.id.third_order_all -> startActivity<OrderActivity>()
            R.id.third_order_pay -> startActivity<OrderActivity>("position" to 1)
            R.id.third_order_out -> startActivity<OrderActivity>("position" to 5)
            R.id.third_plan -> startActivity<PlanMineActivity>()
            R.id.third_put -> startActivity<SettlementActivity>()
            R.id.third_service -> startActivity<ServiceActivity>()
            R.id.third_contact -> startActivity<ContactActivity>()
            R.id.third_platform -> startActivity<PlatformActivity>()

            R.id.first_get, R.id.third_get -> {
                OkGo.post<String>(BaseHttp.certification_info)
                        .tag(this@MainActivity)
                        .headers("token", getString("token"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body())
                                        .optJSONObject("object") ?: JSONObject()
                                val passStatus = obj.optString("pass")
                                when (passStatus) {
                                    "-1" -> showToast("资格认证正在审核中，请耐心等待！")
                                    "1" -> showToast("已通过资格认证！")
                                    "0" -> {
                                        showToast("未通过资格认证，请重新提交！")
                                        startActivity<InfoRealActivity>("title" to "获取资质证书", "hint" to "去认证")
                                    }
                                    else -> startActivity<InfoRealActivity>("title" to "获取资质证书", "hint" to "去认证")
                                }
                            }

                        })
            }
        }
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }
}
