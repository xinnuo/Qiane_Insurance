/**
 * created by 小卷毛, 2018/6/22
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
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Qiane_Insurance
 * 创建人：小卷毛
 * 创建时间：2018-06-22 15:51
 */
data class InsuranceModel( //险种详情
        var addAge: String = "",
        var tlMedianAgeRule: String = "",
        var addAge2: String = "",
        var gtMedianAgeRule: String = "",
        var femaleRule: String = "",
        var startAge: String = "",
        var medianAge: String = "",
        var endAge: String = "",
        var dfInsuredAmount: String = "",
        var dfcopies: String = "",
        var dfcopiesSum: String = "",
        var ichange: String = "",
        var inshow: String = "",
        var pitchOn: String = "",
        var insuranceKindId: String = "",
        var insuranceKindItemIds: String = "",
        var insuranceKindName: String = "",
        var pinsuranceKindItemIds: String = "",
        var type: String = "",
        var lds: List<CommonData>? = ArrayList(),
        var lis: List<CommonData>? = ArrayList(),
        var lrs: List<CommonData>? = ArrayList()
): Serializable