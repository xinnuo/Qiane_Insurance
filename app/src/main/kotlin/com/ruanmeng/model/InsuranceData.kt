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
        var insuranceKindId: String = "",   //险种id
        var insuranceKindName: String = "", //险种
        var insuredAmount: Double = 0.00,   //保额
        var proportion: Double = 0.00,      //费率
        var insuredFee: Double = 0.00,      //保费
        var insuredRange: String = "",      //交费期间
        var isDelete: Boolean = true,       //是否删除
        var listTable: ArrayList<TableData> = ArrayList()
): Serializable