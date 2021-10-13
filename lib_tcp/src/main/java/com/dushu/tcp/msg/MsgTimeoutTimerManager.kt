package com.dushu.tcp.msg


import com.dushu.tcp.TCPInterface
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 2:53 下午
 * @Description:消息发送超时管理器，用于管理消息定时器的新增、移除等
 */
class MsgTimeoutTimerManager(private val tcp: TCPInterface) {
    private val mMsgTimeoutMap: ConcurrentHashMap<String, MsgTimeoutTimer> =
        ConcurrentHashMap<String, MsgTimeoutTimer>()


    /**
     * 添加消息到发送超时管理器
     *
     * @param message
     */
    // TODO: 2021/9/29
    fun add(message: TCPMessage?) {
        if (message?.header == null) {
            return
        }
        val clientReceivedReportMsgType = tcp.getClientReceivedReportMsgType()
        val handshakeMessage: TCPMessage? = tcp.getHandshakeMsg()
        val handshakeMsgName = handshakeMessage?.header?.messageName

        val heartbeatMessage: TCPMessage? = tcp.getHeartbeatMsg()
        val heartbeatMsgName = heartbeatMessage?.header?.messageName
        val messageName = message.header.messageName
        // 握手消息、心跳消息、客户端返回的状态报告消息，不用重发。
        if (messageName == handshakeMsgName || messageName == heartbeatMsgName || messageName == clientReceivedReportMsgType) {
            return
        }
        if (!mMsgTimeoutMap.containsKey(messageName)) {
            val timer = MsgTimeoutTimer(tcp, message)
            mMsgTimeoutMap[messageName!!] = timer
        }
        println("添加消息超发送超时管理器，message=" + message + "\t当前管理器消息数：" + mMsgTimeoutMap.size)
    }

    /**
     * 从发送超时管理器中移除消息，并停止定时器
     *
     * @param msgId
     */
    fun remove(msgId: String?) {
        if (msgId.isNullOrEmpty()) {
            return
        }
        var timer: MsgTimeoutTimer? = mMsgTimeoutMap.remove(msgId)
        timer?.let {
            println("从发送消息管理器移除消息，message=${it.getMsg()}")
            it.cancel()
            timer = null
        }
    }

    /**
     * 重连成功回调，重连并握手成功时，重发消息发送超时管理器中所有的消息
     */
    @Synchronized
    fun sendMsgAll() {
        val it = mMsgTimeoutMap.entries.iterator()
        while (it.hasNext()) {
            it.next().value.sendMsg()
        }
    }
}