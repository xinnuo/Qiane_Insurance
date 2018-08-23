package com.ruanmeng.utils

import java.util.regex.Pattern

object RegexHelper {

    /**
     * 判断密码强度
     * 最少八个字符，至少一个大写字母，一个小写字母和一个数字
     */
    fun passwordStrong(passwordStr: String): Boolean =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
                    .matcher(passwordStr)
                    .matches()

    /**
     * 使用正则表达式去掉多余的.与0
     */
    fun subZeroAndDot(str: String): String =
            when {
                str.indexOf(".") > 0 ->
                    str.replace("0+?$".toRegex(), "")      //去掉多余的0
                            .replace("[.]$".toRegex(), "") //如最后一位是.则去掉
                else -> str
            }
}