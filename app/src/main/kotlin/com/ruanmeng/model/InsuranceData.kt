/**
 * created by 小卷毛, 2018/6/25
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
 * 创建时间：2018-06-25 11:17
 */
data class InsuranceData(
        //险种选择
        var insuranceKindId: String = "",      //险种id
        var insuranceKindName: String = "",    //险种
        var insuredTotalAmount: Double = 0.00, //总保额
        var insuredTotalFee: Double = 0.00,    //总保费
        var isDelete: Boolean = true,          //是否删除
        var insuredTable: ArrayList<TableData> = ArrayList(),
        var insuredItem: ArrayList<InsuranceItemData> = ArrayList(),

        var insuredPosition: Int = 0,
        var insuranceOptDictionaryId: String = "",
        var optDictionaryName: String = "",
        var type: String = "",
        var checkName: String = "",
        var checkItemId: String = "",
        var optDictionaryFee: String = "",
        var insuredParentPosition: Int = 0
) : Serializable