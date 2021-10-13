package com.dushu.tcp

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 10:12 上午
 * @Description:连接状态回调
 */
interface ConnectStatusCallback {
    /**
     * 连接中
     */
    fun onConnecting()

    /**
     * 连接成功
     */
    fun onConnected()

    /**
     * 连接失败
     */
    fun onConnectFailed()
}