/**
 * created by 小卷毛, 2018/9/25
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
package com.ruanmeng.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 项目名称：Qiane_Insurance
 * 创建人：小卷毛
 * 创建时间：2018-09-25 16:05
 */
internal class TabFragmentAdapter(
        fm: FragmentManager,
        private val titles: List<String>,
        private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = titles.size

    // 配置标题的方法
    override fun getPageTitle(position: Int) = titles[position]

    // 动态添加删除的方法
    override fun getItemId(position: Int) =
            if (position < fragments.size) fragments[position].hashCode().toLong()
            else super.getItemId(position)

}