/**
 * created by 小卷毛, 2018/6/14
 * Copyright (c) 2018, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng

import android.support.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.qiane_insurance.BuildConfig
import com.tencent.smtt.sdk.QbSdk
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.Config
import com.umeng.socialize.PlatformConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * 项目名称：Qiane_Insurance
 * 创建人：小卷毛
 * 创建时间：2018-06-14 08:51
 */
class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        initOkGo()

        //极光推送
        JPushInterface.setDebugMode(BuildConfig.LOG_DEBUG) //设置开启日志,发布时请关闭日志
        JPushInterface.init(this@Application)              //初始化 JPush

        //友盟分享
        UMConfigure.init(this@Application, "5a77bcc9f29d9873ee0000b4", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
        PlatformConfig.setWeixin("wx4df7cabb4727a628", "e08739f5003b3b09f94e403db5ddce3a")
        PlatformConfig.setQQZone("1106641891", "9rE1bLKqw2biHQ0T")
        Config.isJumptoAppStore = true
        UMConfigure.setLogEnabled(BuildConfig.LOG_DEBUG)

        QbSdk.initX5Environment(this@Application, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {}
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            override fun onViewInitFinished(isX5Core: Boolean) = OkLogger.d("initX5Environment: $isX5Core")
        })
    }

    private fun initOkGo() {
        val builder = OkHttpClient.Builder()

        OkLogger.debug("Qiane_Insurance", BuildConfig.LOG_DEBUG)

        //log相关
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO)                        //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor)                          //添加OkGo默认debug日志

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)    //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)   //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS) //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        builder.cookieJar(CookieJarImpl(DBCookieStore(this))) //使用数据库保持cookie，如果cookie不过期，则一直有效
        // builder.cookieJar(CookieJarImpl(SPCookieStore(this)))        //使用sp保持cookie，如果cookie不过期，则一直有效
        // builder.cookieJar(CookieJarImpl(MemoryCookieStore()))        //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，信任所有证书，不安全有风险
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier) //配置https的域名匹配规则

        // 其他统一的配置
        OkGo.getInstance().init(this@Application)              //必须调用初始化
                .setOkHttpClient(builder.build())              //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)              //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)  //全局统一缓存时间，默认永不过期，可以不传
                .retryCount = 3                                //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }
}