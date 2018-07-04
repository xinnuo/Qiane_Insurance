package com.ruanmeng.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.qiane_insurance.R
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.fragment_main_third.*
import org.jetbrains.anko.support.v4.dip
import org.json.JSONObject
import java.text.DecimalFormat

class MainThirdFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun init_title() {
        third_title_ll.setBackgroundColor(Color.argb(0, 194, 13, 35))

        third_scroll.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY > dip(100)) {
                val color = Color.argb(255, 194, 13, 35)
                third_title_ll.setBackgroundColor(color)
            }
            else {
                val alpha = scrollY / dip(100) * 255
                val color = Color.argb(alpha, 194, 13, 35)
                third_title_ll.setBackgroundColor(color)
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_msg_data)
                .tag(this@MainThirdFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(activity, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("userMsg") ?: JSONObject()
                        putString("nickName", Tools.decodeUnicode(obj.optString("nickName")))
                        putString("realName", obj.optString("realName"))
                        putString("userhead", obj.optString("userhead"))
                        putString("sex", obj.optString("sex", "1"))
                        putString("pass", obj.optString("pass"))
                        putString("companyId", obj.optString("companyId"))
                        putString("balance", obj.optStringNotEmpty("balance", "0.00"))
                        putString("integral", obj.optStringNotEmpty("integral", "0"))

                        third_name.text = getString("nickName")
                        third_income.text = DecimalFormat("0.00").format(getString("balance").toDouble())
                        third_point.text = DecimalFormat("0.##").format(getString("integral").toDouble())

                        if (third_img.getTag(R.id.third_img) == null) {
                            third_img.loadImage(BaseHttp.baseImg + getString("userhead"))
                            third_img.setTag(R.id.third_img, getString("userhead"))
                        } else {
                            if (third_img.getTag(R.id.third_img) != getString("userhead")) {
                                third_img.loadImage(BaseHttp.baseImg + getString("userhead"))
                                third_img.setTag(R.id.third_img, getString("userhead"))
                            }
                        }
                    }

                })
    }
}
