package com.dushu.tcp.annotation

import androidx.annotation.IntDef

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 11:30 上午
 * @Description:连接状态
 */

// 连接状态：连接中
const val CONNECT_STATE_CONNECTING = 0

// 连接状态：连接成功
const val CONNECT_STATE_SUCCESSFUL = 1

// 连接状态：连接失败
const val CONNECT_STATE_FAILURE = -1

@IntDef(CONNECT_STATE_CONNECTING, CONNECT_STATE_SUCCESSFUL, CONNECT_STATE_FAILURE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.TYPE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION)
annotation class ConnectStatus


