package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.cancelLoadingDialog
import com.ruanmeng.base.getString
import com.ruanmeng.base.showLoadingDialog
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DESUtil
import com.ruanmeng.utils.EncryptUtil
import com.ruanmeng.utils.Tools
import com.ruanmeng.utils.isWeb
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
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.webView
import org.jsoup.Jsoup

class ShareActivity : BaseActivity() {

    private lateinit var webView: WebView
    private var mTitle = ""
    private var mContent = ""

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

            addJavascriptInterface(JsInteration(), "share")
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    mTitle = title
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
        init_title("邀请有奖")

        webView.loadUrl(BaseHttp.invite_index + getString("token"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@ShareActivity).onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun showShareDialog() {
        EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
        val userInfoId = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), getString("token"))
        val urlShare = BaseHttp.register_index + userInfoId

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
                        title = mTitle
                        description = mContent
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
                        title = mTitle
                        description = mContent
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
                        title = mTitle
                        description = mContent
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
                        title = mTitle
                        description = mContent
                        setThumb(UMImage(baseContext, Tools.getViewBitmap(webView)))
                    })
                    .share()
        }
        cancel.onClick { dialog.dismiss() }

        dialog.setContentView(view)
        dialog.show()
    }

    inner class JsInteration {
        @JavascriptInterface
        fun openDialog() {
                Flowable.just(BaseHttp.invite_index + getString("token"))
                        .map { return@map Jsoup.connect(it).get() }
                        .subscribeOn(Schedulers.newThread())
                        .doOnSubscribe { showLoadingDialog() }
                        .doFinally { cancelLoadingDialog() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val items = it.select("div.jl-list").select("div.item")
                            val strHint = StringBuilder()
                            items.forEachWithIndex { index, item ->
                                strHint.append(item.select("div.jl-text").select("span").text())
                                strHint.append(if (index == items.size - 1) "。" else "，")
                            }
                            mContent = strHint.toString().replace(" ", "")

                            showShareDialog()
                        }
        }
    }
}
