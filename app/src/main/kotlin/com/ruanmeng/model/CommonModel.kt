/**
 * created by 小卷毛, 2018/6/21
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
 * 创建时间：2018-06-21 15:57
 */
data class CommonModel(
        var slider: List<CommonData> ?= ArrayList(),
        var news: List<CommonData> ?= ArrayList(),

        //计划书详情
        var companyId: String = "",
        var productId: String = "",
        var prospectusId: String = "",
        var lbs: List<InsuranceModel>? = ArrayList(),
        var lks: List<CommonData>? = ArrayList(),

        //计划书阅读
        var birthCtn: String = "",
        var customerCtn: String = "",
        var ordeDayCtn: String = "",
        var birthList: List<CommonData>? = ArrayList(),
        var customerList: List<CommonData>? = ArrayList(),

        //计划书阅读
        var makeSum: String = "",
        var readSum: String = "",
        var lwithin: List<CommonData>? = ArrayList(),
        var lago: List<CommonData>? = ArrayList(),

        //保险种类
        var insuranceTypeId: String = "",
        var insuranceTypeName: String = "",
        var lcs: List<CommonData>? = ArrayList()
): Serializable