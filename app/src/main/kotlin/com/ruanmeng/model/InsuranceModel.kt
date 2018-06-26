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
        var addAge: String = "0", //大
        var tlMedianAgeRule: String = "",
        var addAge2: String = "", //小
        var gtMedianAgeRule: String = "",
        var femaleRule: String = "",
        var startAge: String = "",
        var medianAge: String = "0",
        var endAge: String = "",
        var dfInsuredAmount: String = "",
        var dfcopies: String = "",
        var dfcopiesSum: String = "",
        var ichange: String = "", //是否可以更改
        var inshow: String = "",  //是否默认显示
        var pitchOn: String = "", //是否默认选中
        var insuranceKindId: String = "",
        var insuranceKindItemIds: String = "",
        var insuranceKindName: String = "",
        var pinsuranceKindItemIds: String = "",
        var type: String = "",                    // "1":"主险", "2":"附加险", "3":"豁免相关", "4":"双豁免"
        var baseProportion: Double = 0.00,        //基准费率
        var proportion: Double = 0.00,            //选择费率
        var lds: List<CommonData>? = ArrayList(), //险种选项列表
        var lis: List<CommonData>? = ArrayList(), //险种选项选择列表
        var lrs: List<CommonData>? = ArrayList(), //险种项与保额保费关系列表
        var insuredParentPosition: Int = 0,
        var isGray: Boolean = false,      //是否灰色
        var isClickable: Boolean = true,  //是否点击
        var isExpand: Boolean = true,     //是否展开
        var isChecked: Boolean = false    //是否选中
): Serializable