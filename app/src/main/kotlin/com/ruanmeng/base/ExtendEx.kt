/**
 * created by 小卷毛, 2018/4/21 0021
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
package com.ruanmeng.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import org.json.JSONObject
import java.util.concurrent.TimeUnit

inline fun <reified T : View> T.visible() {
    visibility = View.VISIBLE
}

inline fun <reified T : View> T.invisible() {
    visibility = View.INVISIBLE
}

inline fun <reified T : View> T.gone() {
    visibility = View.GONE
}

fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Returns the value mapped by name if it exists, coercing it if
 * necessary, or fallback if no such mapping exists or empty.
 */
inline fun <reified T : JSONObject> T.optStringNotEmpty(name: String, fallback: String): String =
        if (optString(name, fallback).isEmpty()) fallback else optString(name, fallback)

/**
 * 防抖动点击事件，时间单位秒（默认1s）
 */
inline fun <reified T : View> T.setOneClickListener(onClickListener: View.OnClickListener) {
    RxView.clicks(this).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                onClickListener.onClick(this)
            }
}

/**
 * 防抖动点击事件，默认时间1s，默认单位秒
 */
inline fun <reified T : View> T.setOneClickListener(duration: Long = 1,
                                                    unit: TimeUnit = TimeUnit.SECONDS,
                                                    onClickListener: View.OnClickListener) {
    RxView.clicks(this).throttleFirst(duration, unit)
            .subscribe {
                onClickListener.onClick(this)
            }
}