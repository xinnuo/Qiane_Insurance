/**
 * created by 小卷毛, 2018/6/30
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
package com.ruanmeng.view

import android.view.ViewManager
import com.tencent.smtt.sdk.WebView
import org.jetbrains.anko.custom.ankoView

/**
 * 项目名称：Qiane_Insurance
 * 创建人：小卷毛
 * 创建时间：2018-06-30 18:02
 */
inline fun ViewManager.webViewX5(theme: Int = 0) = webViewX5(theme) {}

inline fun ViewManager.webViewX5(theme: Int = 0, init: WebView.() -> Unit) = ankoView({ WebView(it) }, theme, init)

