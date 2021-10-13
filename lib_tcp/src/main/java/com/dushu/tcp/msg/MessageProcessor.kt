package com.dushu.tcp.msg

import android.util.Log
import com.dushu.tcp.app.TCPManager
import com.dushu.tcp.util.GsonUtil


class MessageProcessor private constructor() : IMessageProcessor {
    private val TAG: String = MessageProcessor::class.java.simpleName

    companion object {
        val INSTANCE = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = MessageProcessor()
    }

    override fun receiveMsg(message: TCPMessage?) {
        Log.e(TAG, "receiveMsg=" + message)
        val head = TCPMessageHead()
        head.messageName = "_MS:Ping"
        head.contentType = 0
        val message = TCPMessage(head)
        message.content =
            GsonUtil.GsonString(mapOf("timestamp" to System.currentTimeMillis()))
        sendMsg(message)

    }

    override fun sendMsg(message: TCPMessage?) {
        val isActive: Boolean = TCPManager.INSTANCE.isActive()
        if (isActive) {
            TCPManager.INSTANCE
                .sendMessage(message)
        } else {
            Log.e(TAG, "发送消息失败")
        }

    }


}