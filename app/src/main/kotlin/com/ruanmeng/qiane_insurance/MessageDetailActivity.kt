package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.isWeb
import kotlinx.android.synthetic.main.activity_message_detail.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk25.listeners.onClick
import org.json.JSONObject

class MessageDetailActivity : BaseActivity() {

    private var messageStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        init_title("消息详情")

        getData()

        val str = "<!doctype html><html>\n" +
                "<meta charset=\"utf-8\">" +
                "<style type=\"text/css\">" +
                "body{ padding:0; margin:0; }\n" +
                ".con{ width:95%; margin:0 auto; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                "*{ max-width:100% !important; }\n" +
                "</style>\n" +
                "<body style=\"padding:0; margin:0; \">" +
                "<div class=\"con\">" +
                intent.getStringExtra("content") +
                "</div>" +
                "</body>" +
                "</html>"

        msg_web.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        msg_web.apply {
            overScrollMode = View.OVER_SCROLL_NEVER

            //支持javascript
            settings.javaScriptEnabled = true
            //设置可以支持缩放
            settings.setSupportZoom(true)
            //自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            //设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            //设置是否使用缓存
            settings.setAppCacheEnabled(true)
            settings.domStorageEnabled = true

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {

                /* 这个事件，将在用户点击链接时触发。
                 * 通过判断url，可确定如何操作，
                 * 如果返回true，表示我们已经处理了这个request，
                 * 如果返回false，表示没有处理，那么浏览器将会根据url获取网页
                 */
                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                    if (!url.isWeb()) return true

                    if (url.isNotEmpty() && url.endsWith("apk")) browse(url)
                    else {
                        view.loadUrl(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }

            }
        }

        val mHint = intent.getStringExtra("hint")
        msg_title.text = mHint
        msg_img.visibility = if (mHint == "互动消息") View.VISIBLE else View.INVISIBLE
        msg_time.text = intent.getStringExtra("time")

        msg_img.onClick {
            if (messageStatus.isEmpty()) {
                showToast("获取状态信息失败")
                return@onClick
            }

            OkGo.post<String>(
                    if (messageStatus == "1") BaseHttp.cancel_msg_praise
                    else BaseHttp.add_msg_praise)
                    .tag(this@MessageDetailActivity)
                    .headers("token", getString("token"))
                    .params("msgReceiveId", intent.getStringExtra("msgId"))
                    .execute(object : StringDialogCallback(baseContext, false) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            showToast(msg)
                            messageStatus = if (messageStatus == "1") "0" else "1"
                            msg_img.setImageResource(if (messageStatus == "1") R.mipmap.msg_icon02 else R.mipmap.msg_icon01)
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.find_msg_praise)
                .tag(this@MessageDetailActivity)
                .headers("token", getString("token"))
                .params("msgReceiveId", intent.getStringExtra("msgId"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        messageStatus = JSONObject(response.body()).optString("object")
                        msg_img.setImageResource(if (messageStatus == "1") R.mipmap.msg_icon02 else R.mipmap.msg_icon01)
                    }

                })
    }
}
