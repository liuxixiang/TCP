package com.dushu.tcp

import com.dushu.tcp.msg.TCPMessage


/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 10:11 上午
 * @Description:与应用层交互的listener
 */
interface OnTCPEventListener {
    /**
     * 分发消息到应用层
     *
     * @param message
     */
    fun dispatchMsg(message: TCPMessage?)

    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    fun isNetworkAvailable(): Boolean

    /**
     * 获取由应用层构造的握手消息
     *
     * @return
     */
    fun getHandshakeMsg(): TCPMessage?

    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    fun getHeartbeatMsg(): TCPMessage?

    /**
     * 获取应用层消息接收状态报告消息类型
     *
     * @return
     */
    fun getClientReceivedReportMsgType(): String


}