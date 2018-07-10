package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getBoolean
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.share.Const
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

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
            R.id.first_search -> startActivity<SearchActivity>()
            R.id.first_message -> startActivity<MessageActivity>()
            R.id.first_get -> startActivity<InfoRealActivity>("title" to "获取资质证书", "hint" to "去认证")
            R.id.first_plan -> startActivity<PlanActivity>()
            R.id.first_tool -> startActivity<ToolActivity>()
            R.id.first_share -> startActivity<ShareActivity>()
            R.id.first_company -> startActivity<CompanyActivity>("type" to "选择公司")

            R.id.second_search -> startActivity<SearchActivity>()
            R.id.second_total_ll -> startActivity<ClientActivity>()
            R.id.second_read_ll -> startActivity<ReadActivity>()

            R.id.third_setting -> startActivity<SettingActivity>()
            R.id.third_message -> startActivity<MessageActivity>()
            R.id.third_info -> startActivity<InfoActivity>()
            R.id.third_card -> startActivity<CardActivity>()
            R.id.third_income_ll -> startActivity<IncomeActivity>()
            R.id.third_point_ll -> startActivity<PointActivity>()
            R.id.third_order, R.id.third_order_all  -> startActivity<OrderActivity>()
            R.id.third_order_pay -> startActivity<OrderActivity>("position" to 1)
            R.id.third_order_out -> startActivity<OrderActivity>("position" to 2)
            R.id.third_get -> startActivity<InfoRealActivity>("title" to "获取资质证书", "hint" to "去认证")
            R.id.third_guest -> startActivity<ClientActivity>()
            R.id.third_plan -> startActivity<PlanMineActivity>()
            R.id.third_put -> startActivity<WebActivity>("title" to "祁安理赔")
            R.id.third_share -> startActivity<ShareActivity>()
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
