package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.showToast
import com.ruanmeng.qiane_insurance.R
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.fragment_phone_first.*

class PhoneFirstFragment : BaseFragment() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        bt_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_next.isClickable = false
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        et_card.addTextChangedListener(this)
        et_tel.addTextChangedListener(this)
        et_yzm.addTextChangedListener(this)

        bt_yzm.setOnClickListener {
            if (et_tel.text.isBlank()) {
                et_tel.requestFocus()
                showToast("请输入手机号")
                return@setOnClickListener
            }

            if (!CommonUtil.isMobile(et_tel.text.toString())) {
                et_tel.requestFocus()
                et_tel.setText("")
                showToast("手机号码格式错误，请重新输入")
                return@setOnClickListener
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

        bt_next.setOnClickListener {
            (activity as OnFragmentItemSelectListener).onitemSelected(
                    "下一步", "", "")
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_card.text.isNotBlank()
                && et_tel.text.isNotBlank()
                && et_yzm.text.isNotBlank()) {
            bt_next.setBackgroundResource(R.drawable.rec_bg_red)
            bt_next.isClickable = true
        } else {
            bt_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_next.isClickable = false
        }
    }
}
