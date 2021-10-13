package com.dushu.tcp.annotation

import androidx.annotation.IntDef

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 11:30 上午
 * @Description:连接状态
 */

// 应用在前台标识
const val APP_STATUS_FOREGROUND = 0

// 应用在后台标识
const val APP_STATUS_BACKGROUND = -1

@IntDef(APP_STATUS_FOREGROUND, APP_STATUS_BACKGROUND)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class AppStatus


