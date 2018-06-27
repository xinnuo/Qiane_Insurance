package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.webView

class PlanLookActivity : BaseActivity() {

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
            settings.useWideViewPort = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            //设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            //设置是否使用缓存
            settings.setAppCacheEnabled(true)
            settings.domStorageEnabled = true

            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    tvTitle.text = title

                    when (intent.getStringExtra("type")) {
                        "计划书" -> {
                            val hint = intent.getStringExtra("title")
                            if (hint == title) ivRight.visible() else ivRight.gone()
                        }
                        "产品详情" -> if (title.contains("产品详情")) ivRight.visible() else ivRight.gone()
                    }
                }
            }
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
        init_title()

        when (intent.getStringExtra("type")) {
            "计划书" -> webView.loadUrl(BaseHttp.prospectus_open + intent.getStringExtra("prospectusId"))
            "产品详情" -> webView.loadUrl(BaseHttp.product_detils + intent.getStringExtra("productinId"))
        }
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.iv_nav_right -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_share_bottom, null) as View
                val wechat = view.findViewById<LinearLayout>(R.id.dialog_share_wechat)
                val circle = view.findViewById<LinearLayout>(R.id.dialog_share_circle)
                val qq = view.findViewById<LinearLayout>(R.id.dialog_share_qq)
                val space = view.findViewById<LinearLayout>(R.id.dialog_share_space)
                val cancel = view.findViewById<Button>(R.id.dialog_share_cancel)
                val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

                wechat.onClick { dialog.dismiss() }
                circle.onClick { dialog.dismiss() }
                qq.onClick { dialog.dismiss() }
                space.onClick { dialog.dismiss() }
                cancel.onClick { dialog.dismiss() }

                dialog.setContentView(view)
                dialog.show()
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
