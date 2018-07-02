package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.cancelLoadingDialog
import com.ruanmeng.base.showLoadingDialog
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
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

            webViewClient = object : WebViewClient() {

                /* 这个事件，将在用户点击链接时触发。
                 * 通过判断url，可确定如何操作，
                 * 如果返回true，表示我们已经处理了这个request，
                 * 如果返回false，表示没有处理，那么浏览器将会根据url获取网页
                 */
                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                    if (!CommonUtil.isWeb(url)) return true

                    if (url.isNotEmpty() && url.endsWith("apk")) browse(url)
                    else {
                        view.loadUrl(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }

                /*
                 * 在开始加载网页时会回调
                 */
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoadingDialog()
                }

                /*
                 * 在结束加载网页时会回调
                 */
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    cancelLoadingDialog()
                }
            }
        }
        init_title(intent.getStringExtra("title"))

        when (intent.getStringExtra("title")) {
            "祁安理赔" -> {
                webView.loadUrl("https://www.baidu.com/")
            }
            "关于我们" -> {
                webView.loadUrl("https://www.baidu.com/")
            }
            "注册协议" -> {
                webView.loadUrl("https://www.baidu.com/")
            }
            "提现规则" -> {
                webView.loadUrl("https://www.baidu.com/")
            }
            "积分规则" -> {
                webView.loadUrl("https://www.baidu.com/")
            }
            "公司介绍" -> {
                OkGo.post<String>(BaseHttp.company_info)
                        .tag(this@WebActivity)
                        .params("companyId", intent.getStringExtra("companyId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()
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

                                val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()
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
