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

        //计划书列表
        var prospectusId: String = "",
        var prospectusImg: String = "",
        var prospectusTitle: String = "",

        //险种列表
        var insuranceKindId: String = "",
        var insuranceKindName: String = "",

        //险种选项列表
        var insuranceOptDictionaryId: String = "",
        var optDictionaryName: String = "",
        var insuredParentPosition: Int = 0,
        var checkName: String = "",
        var checkItemId: String = "",

        //险种选项选择列表
        var insuranceItemId: String = "",
        var itemName: String = "",
        var checkItem: String = "",

        //险种项与保额保费关系列表
        var insuranceKindItemRefId: String = "",
        var item1: String = "",
        var item2: String = "",
        var item3: String = "",
        var item4: String = "",
        var proportion: String = "",

        //计划书阅读列表
        var coverSex: String = "",
        var coverAge: String = "",
        var insuranceSum: String = "",
        var lastReadTime: String = "",

        //产品列表
        var productinId: String = "",
        var productImg: String = "",
        var productName: String = "",
        var synopsis: String = "",
        var productSum: String = "",
        var recommendSum: String = "",

        //保险种类
        var insuranceTypeId: String = "",
        var insuranceTypeName: String = "",

        //订单列表
        var goodsOrderId: String = "",
        var payTime: String = "",
        var buyName: String = "",
        var coverName: String = "",
        var startDate: String = "",
        var endDate: String = "",
        var spreadRate: String = "",
        var payCost: String = "",
        var status: String = "",

        //资讯列表
        var informationId: String = "",
        var informationHead: String = "",
        var informationTitle: String = "",
        var createDate: String = "",

        //提现列表
        var refuseReason: String = "",
        var withdrawSum: String = "",
        var withdrawWay: String = "",

        //收益列表
        var profitExplain: String = "",
        var profitSum: String = "",

        //公司列表
        var companyId: String = "",
        var companyName: String = "",

        //消息列表
        var msgReceiveId: String = "",
        var sendDate: String = "",
        var content: String = "",

        //客户列表
        var customerBirth: String = "",
        var usercustomerId: String = "",
        var customerName: String = "",
        var fristName: String = "",
        var year: String = "",
        var day: String = "",

        //添加客户列表
        var certificateType: String = "",
        var certificateNumber: String = "",
        var certificateStartDate: String = "",
        var certificateEndDate: String = "",
        var longValid: String = "0",
        var province: String = "",
        var city: String = "",
        var district: String = "",
        var detailAdress: String = "",
        var postcode: String = "",

        //地区列表
        var areaCode: String = "",
        var areaId: String = "",
        var areaName: String = "",

        var isChecked: Boolean = false
) : Serializable