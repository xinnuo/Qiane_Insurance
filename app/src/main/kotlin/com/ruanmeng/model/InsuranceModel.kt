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
        var startAge: String = "",
        var medianAge: String = "0",
        var endAge: String = "",
        var dfInsuredAmount: String = "",
        var dfcopies: String = "",
        var dfcopiesSum: String = "",
        var cascade: String = "",  //是否选择保额、保费
        var homes: String = "",    //是否家庭版
        var homeHint: String = "", //家庭版标题
        var maxpeple: String = "", //最大成员数
        var ichange: String = "",  //是否可以更改
        var inshow: String = "",   //是否默认显示
        var pitchOn: String = "",  //是否默认选中
        var pitems: String = "",   //选项联动id
        var pitemsName: String = "",
        var insuranceKindId: String = "",
        var insuranceKindName: String = "",
        var insuranceKindItemIds: String = "",
        var pinsuranceKindItemIds: String = "",
        var type: String = "",                    // "1":"主险", "2":"附加险", "3":"豁免相关", "4":"双豁免"
        var proportionType: String = "",          //费率方式
        var proportionAllow: String = "",         //是否允许
        var proportion: Double = 0.00,            //选择费率
        var insuredAmount: Double = 0.00,         //选择保额
        var premium: Double = 0.00,               //选择保费
        var lds: ArrayList<CommonData>? = ArrayList(), //险种选项列表
        var lis: ArrayList<CommonData>? = ArrayList(), //险种选项选择列表
        var lrs: ArrayList<CommonData>? = ArrayList(), //险种项、年龄与保额保费关系列表
        var insuredParentPosition: Int = 0,
        var isGray: Boolean = false,      //是否灰色
        var isClickable: Boolean = true,  //是否点击
        var isExpand: Boolean = true,     //是否展开
        var isChecked: Boolean = false    //是否选中
): Serializable