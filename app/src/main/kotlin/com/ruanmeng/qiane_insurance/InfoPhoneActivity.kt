package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.OnFragmentItemSelectListener
import com.ruanmeng.fragment.PhoneFirstFragment
import com.ruanmeng.fragment.PhoneSecondFragment
import com.ruanmeng.utils.ActivityStack
import org.jetbrains.anko.frameLayout

class InfoPhoneActivity : BaseActivity(), OnFragmentItemSelectListener {

    private val mContainer = 1
    private lateinit var first: PhoneFirstFragment
    private lateinit var second: PhoneSecondFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init_title("验证手机号码")

        frameLayout { id = mContainer }

        supportFragmentManager.addOnBackStackChangedListener {
            tvTitle.text = when (supportFragmentManager.backStackEntryCount) {
                1 -> "更换手机号码"
                else -> "验证手机号码"
            }
        }

        first = PhoneFirstFragment()
        supportFragmentManager
                .beginTransaction()
                .add(mContainer, first)
                .commit()
    }

    override fun onitemSelected(type: String, id: String, name: String) {
        when (type) {
            "下一步" -> {
                second = PhoneSecondFragment()
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.push_left_in,
                                R.anim.push_left_out,
                                R.anim.push_right_in,
                                R.anim.push_right_out)
                        .replace(mContainer, second)
                        .addToBackStack(null)
                        .commit()
            }
            "确定" -> ActivityStack.screenManager.popActivities(this::class.java)
        }
    }
}
