package com.dushu.tcp.msg

import com.dushu.tcp.OnTCPEventListener


/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 10:45 上午
 * @Description: 消息转发器，负责将接收到的消息转发到应用层
 */
class MsgDispatcher(private var listener: OnTCPEventListener?) {
    fun setOnKeepAliveEventListener(listener: OnTCPEventListener) {
        this.listener = listener
    }

    /**
     * 接收消息，并通过OnEventListener转发消息到应用层
     * @param message
     */
    fun receivedMsg(message: TCPMessage?) {
        listener?.dispatchMsg(message)
    }
}