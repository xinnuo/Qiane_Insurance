package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.showToast
import com.ruanmeng.qiane_insurance.R
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.fragment_phone_second.*
import org.jetbrains.anko.sdk25.listeners.onClick

class PhoneSecondFragment : BaseFragment() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        // setOnClickListener();后会默认设置setClickable=true
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        et_tel.addTextChangedListener(this)
        et_yzm.addTextChangedListener(this)

        bt_yzm.onClick {
            if (et_tel.text.isBlank()) {
                et_tel.requestFocus()
                showToast("请输入手机号")
                return@onClick
            }

            if (!et_tel.text.toString().isMobile()) {
                et_tel.requestFocus()
                et_tel.setText("")
                showToast("手机号码格式错误，请重新输入")
                return@onClick
            }

            thread = Runnable {
                bt_yzm.text = "${time_count}秒后重发"
                if (time_count > 0) {
                    bt_yzm.postDelayed(thread, 1000)
                    time_count--
                } else {
                    bt_yzm.text = "获取验证码"
                    bt_yzm.isClickable = true
                    time_count = 180
                }
            }
        }

        bt_ok.onClick {
            (activity as OnFragmentItemSelectListener).onitemSelected(
                    "确定", "", "")
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_tel.text.isNotBlank()
                && et_yzm.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_red)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
