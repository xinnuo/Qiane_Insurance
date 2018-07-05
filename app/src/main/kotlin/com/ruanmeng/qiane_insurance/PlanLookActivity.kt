package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DESUtil
import com.ruanmeng.utils.EncryptUtil
import com.ruanmeng.utils.Tools
import com.ruanmeng.utils.isWeb
import com.ruanmeng.view.webViewX5
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.browse
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jsoup.Jsoup

class PlanLookActivity : BaseActivity() {

    private lateinit var webView: WebView

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        frameLayout {
            webView = webViewX5 {
                settings.apply {
                    //支持javascript
                    javaScriptEnabled = true
                    //设置可以支持缩放
                    setSupportZoom(true)
                    //自适应屏幕
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

                    //设置出现缩放工具
                    builtInZoomControls = true
                    displayZoomControls = false

                    //设置是否使用缓存
                    setAppCacheEnabled(true)
                    domStorageEnabled = true

                    allowFileAccess = true
                    useWideViewPort = true
                    //开启 Application Caches 功能
                    setAppCacheEnabled(true)
                    //设置 Application Caches 缓存目录
                    setAppCachePath(cacheDir.absolutePath)
                    setGeolocationEnabled(true)
                    setGeolocationDatabasePath(cacheDir.absolutePath)
                    databaseEnabled = true
                    databasePath = cacheDir.absolutePath
                }
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER

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

                        if (url.contains("tel:")) makeCall(url.replace("tel:", ""))

                        if (!url.isWeb()) return true

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
        }

        init_title()

        when (intent.getStringExtra("type")) {
            "计划书" -> {
                EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
                val userInfoId = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), getString("token"))
                webView.loadUrl(BaseHttp.prospectus_open + intent.getStringExtra("prospectusId") + "&userInfoId=$userInfoId")
            }
            "产品详情" -> {
                EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
                val userInfoId = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), getString("token"))
                webView.loadUrl(BaseHttp.product_detils + intent.getStringExtra("productinId") + "&userInfoId=$userInfoId")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@PlanLookActivity).onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun showShareDialog(content: String, urlShare: String) {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_share_bottom, null) as View
        val wechat = view.findViewById<LinearLayout>(R.id.dialog_share_wechat)
        val circle = view.findViewById<LinearLayout>(R.id.dialog_share_circle)
        val qq = view.findViewById<LinearLayout>(R.id.dialog_share_qq)
        val space = view.findViewById<LinearLayout>(R.id.dialog_share_space)
        val cancel = view.findViewById<Button>(R.id.dialog_share_cancel)
        val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

        wechat.onClick {
            dialog.dismiss()

            ShareAction(baseContext)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(getString(R.string.app_name))
                    .withMedia(UMWeb(urlShare).apply {
                        title = tvTitle.text.toString()
                        description = content
                        setThumb(UMImage(baseContext, Tools.getViewBitmap(webView)))
                    })
                    .share()
        }
        circle.onClick {
            dialog.dismiss()

            ShareAction(baseContext)
                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(getString(R.string.app_name))
                    .withMedia(UMWeb(urlShare).apply {
                        title = tvTitle.text.toString()
                        description = content
                        setThumb(UMImage(baseContext, Tools.getViewBitmap(webView)))
                    })
                    .share()
        }
        qq.onClick {
            dialog.dismiss()

            ShareAction(baseContext)
                    .setPlatform(SHARE_MEDIA.QQ)
                    .withText(getString(R.string.app_name))
                    .withMedia(UMWeb(urlShare).apply {
                        title = tvTitle.text.toString()
                        description = content
                        setThumb(UMImage(baseContext, Tools.getViewBitmap(webView)))
                    })
                    .share()
        }
        space.onClick {
            dialog.dismiss()

            ShareAction(baseContext)
                    .setPlatform(SHARE_MEDIA.QZONE)
                    .withText(getString(R.string.app_name))
                    .withMedia(UMWeb(urlShare).apply {
                        title = tvTitle.text.toString()
                        description = content
                        setThumb(UMImage(baseContext, Tools.getViewBitmap(webView)))
                    })
                    .share()
        }
        cancel.onClick { dialog.dismiss() }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.iv_nav_right -> {
                EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
                val userInfoId = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), getString("token"))

                when (intent.getStringExtra("type")) {
                    "计划书" -> {
                        val prospectusId = intent.getStringExtra("prospectusId")
                        val urlShare = BaseHttp.prospectus_detil + "$prospectusId&userInfoId=$userInfoId"

                        Flowable.just(urlShare)
                                .map { return@map Jsoup.connect(it).get() }
                                .subscribeOn(Schedulers.newThread())
                                .doOnSubscribe { showLoadingDialog() }
                                .doFinally { cancelLoadingDialog() }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    val strHint = StringBuilder()
                                    val itemGender = it.select("div.gender").select("span")
                                    itemGender.forEach { strHint.append(it.text()) }
                                    strHint.append("，")

                                    val items = it.select("ul.info-class").select("li")
                                    items.forEachWithIndex { index, item ->
                                        strHint.append(item.select("span").text())
                                        strHint.append(item.select("b.red").text())
                                        strHint.append(if (index == items.size - 1) "。" else "，")
                                    }
                                    val content = strHint.toString().replace(" ", "")

                                    showShareDialog(content, urlShare)
                                }
                    }
                    "产品详情" -> {
                        val productinId = intent.getStringExtra("productinId")
                        val urlShare = BaseHttp.share_product_detils + productinId + "&userInfoId=$userInfoId"

                        Flowable.just(urlShare)
                                .map { return@map Jsoup.connect(it).get() }
                                .subscribeOn(Schedulers.newThread())
                                .doOnSubscribe { showLoadingDialog() }
                                .doFinally { cancelLoadingDialog() }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    val content = it.select("div.container").select("div.item").select("p.text").text()
                                    showShareDialog(content, urlShare)
                                }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack() && intent.getStringExtra("type") == "计划书") {
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
