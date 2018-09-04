package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.makeCall
import com.ruanmeng.share.BaseHttp
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.json.JSONObject

class SettlementActivity : BaseActivity() {

    private var mTel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frameLayout {
            imageView {
                backgroundResource = R.mipmap.settlement_bj
            }.lparams(width = matchParent, height = matchParent)

            verticalLayout {
                view().lparams(height = dip(0)) {
                    weight = 0.52f
                }

                themedButton(R.style.Font14_white_borderless) {
                    backgroundColorResource = R.color.transparent
                    onClick { _ ->
                        alert {
                            title = "拨打电话"
                            message = mTel
                            negativeButton("取消") {}
                            positiveButton("拨打") {
                                if (mTel.isNotEmpty()) makeCall(mTel)
                            }
                        }.show()
                    }
                }.lparams(width = matchParent, height = dip(35)) {
                    leftMargin = dip(80)
                    rightMargin = dip(80)
                }

                view().lparams(height = dip(0)) {
                    weight = 0.48f
                }

            }.lparams(width = matchParent, height = matchParent)
        }
        init_title("祁安理赔")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.help_center)
                .tag(this@SettlementActivity)
                .params("htmlKey", "lpdh")
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body())
                        mTel = obj.optString("help")
                    }

                })
    }
}
