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
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.isWeb
import org.jetbrains.anko.browse
import org.jetbrains.anko.webView
import org.json.JSONObject

class WebActivity : BaseActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = webView {
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
        init_title(intent.getStringExtra("title"))

        when (intent.getStringExtra("title")) {
            "祁安理赔", "关于我们", "注册协议", "提现规则", "积分规则",
            "佣金结算问题", "会员活动问题", "常见问题", "APP使用问题",
            "合作电话", "合作邮箱", "客服电话", "客服QQ", "客服邮箱", "投诉建议电话",
            "公司相关拍照和证件", "授权合作单位列表" -> {
                OkGo.post<String>(BaseHttp.help_center)
                        .tag(this@WebActivity)
                        .params("htmlKey", when (intent.getStringExtra("title")) {
                            "祁安理赔" -> "qnlp"
                            "关于我们" -> "gywm"
                            "注册协议" -> "zcxy"
                            "提现规则" -> "txgz"
                            "积分规则" -> "gfgz"
                            "佣金结算问题" -> "yjjswt"
                            "会员活动问题" -> "hyhdwt"
                            "常见问题" -> "kfcjwt"
                            "APP使用问题" -> "appsywt"
                            "合作电话" -> "hzdh"
                            "合作邮箱" -> "hzyx"
                            "客服电话" -> "kfdh"
                            "客服QQ" -> "kfqq"
                            "客服邮箱" -> "kfyx"
                            "投诉建议电话" -> "tsjydh"
                            "公司相关拍照和证件" -> "gszz"
                            "授权合作单位列表" -> "sqdw"
                            else -> ""
                        })
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body())
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
                                        obj.optString("help") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                webView.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "公司介绍" -> {
                OkGo.post<String>(BaseHttp.company_info)
                        .tag(this@WebActivity)
                        .params("companyId", intent.getStringExtra("companyId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body()).optJSONObject("object")
                                        ?: JSONObject()
                                val str = "<!doctype html><html>\n" +
                                        "<meta charset=\"utf-8\">" +
                                        "<style type=\"text/css\">" +
                                        "body{ padding:0; margin:0; }\n" +
                                        ".view_h1{ width:95%; margin:5px auto 0; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                                        ".con{ width:95%; margin:0 auto; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>\n" +
                                        "<body style=\"padding:0; margin:0; \">" +
                                        "<div class=\"view_h1\" style=\"border-bottom:0.5px solid #ededed; padding-bottom:10px;\">" +
                                        "${obj.optString("companyName")}公司介绍" +
                                        "</div>" +
                                        "<div class=\"con\">" +
                                        obj.optString("companyIntroduce") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                webView.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "资讯详情" -> {
                OkGo.post<String>(BaseHttp.information_detail)
                        .tag(this@WebActivity)
                        .params("informationId", intent.getStringExtra("informationId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body()).optJSONObject("object")
                                        ?: JSONObject()
                                val str = "<!doctype html><html>\n" +
                                        "<meta charset=\"utf-8\">" +
                                        "<style type=\"text/css\">" +
                                        "body{ padding:0; margin:0; }\n" +
                                        ".view_h1{ width:95%; margin:5px auto 0; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                                        ".con{ width:95%; margin:0 auto; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>\n" +
                                        "<body style=\"padding:0; margin:0; \">" +
                                        "<div class=\"view_h1\">" +
                                        obj.optString("informationTitle") +
                                        "</div>" +
                                        "<div class=\"view_time\" style=\"border-bottom:0.5px solid #ededed; padding-bottom:10px;\">" +
                                        obj.optString("createDate") +
                                        "</div>" +
                                        "<div class=\"con\">" +
                                        obj.optString("informationContent") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                webView.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "详情" -> {
                tvTitle.text = intent.getStringExtra("hint")
                val urlLoad = intent.getStringExtra("url")
                webView.loadUrl(if (urlLoad.startsWith("http")) urlLoad else BaseHttp.baseImg + urlLoad)
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
