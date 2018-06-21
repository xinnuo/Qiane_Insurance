/**
 * created by 小卷毛, 2017/10/26
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
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
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-05 16:35
 */
data class CommonData(
        //首页轮播图
        var href: String = "",
        var sliderId: String = "",
        var sliderImg: String = "",

        //首页列表
        var title: String = "",
        var img: String = "",
        var productprospectusId: String = "",
        var type: String = "",

        var content: String = "",
        var price: String = "",

        //计划列表
        var prospectusId: String = "",
        var prospectusImg: String = "",
        var prospectusTitle: String = "",

        //产品列表
        var productinId: String = "",
        var productImg: String = "",
        var productName: String = "",
        var synopsis: String = "",
        var productSum: String = "",
        var recommendSum: String = "",

        //资讯列表
        var informationId: String = "",
        var informationHead: String = "",
        var informationTitle: String = "",
        var createDate: String = "",

        //公司列表
        var companyId: String = "",
        var companyName: String = "",

        //公司列表
        var msgReceiveId: String = "",
        var sendDate: String = "",

        var position: Int = -1,
        var letter: String = "",
        var isClickable: Boolean = true,
        var isChecked: Boolean = false
) : Serializable