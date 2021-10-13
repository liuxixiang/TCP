package com.dushu.tcp.msg


import com.dushu.tcp.TCPInterface
import java.util.*

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 2:54 下午
 * @Description:消息发送超时定时器，每一条消息对应一个定时器
 */
class MsgTimeoutTimer(private val tcp: TCPInterface, private val message: TCPMessage) :
    Timer() {
    private var currentResendCount = 0 // 当前重发次数
    private var task: MsgTimeoutTask? // 消息发送超时任务

    init {
        task = MsgTimeoutTask()
        schedule(task,
            tcp.getTCPConfig().builder.reconnectInterval.toLong(),
            tcp.getTCPConfig().builder.resendInterval.toLong())
    }

    /**
     * 消息发送超时任务
     */
    inner class MsgTimeoutTask : TimerTask() {
        override fun run() {
            if (tcp.isClosed()) {
                tcp.getMsgTimeoutTimerManager()?.remove(message.header?.messageName)
                return
            }
            currentResendCount++
            if (currentResendCount > tcp.getTCPConfig().builder.resendCount) {
                // 重发次数大于可重发次数，直接标识为发送失败，并通过消息转发器通知应用层
                try {
                    // TODO: 2021/9/29

                    // 通知应用层消息发送失败
                    tcp.getMsgDispatcher()?.receivedMsg(null)
                } finally {
                    // 从消息发送超时管理器移除该消息
                    tcp.getMsgTimeoutTimerManager()?.remove(message.header?.messageName)
                    // 执行到这里，认为连接已断开或不稳定，触发重连
                    tcp.resetConnect()
                    currentResendCount = 0
                }
            } else {
                // 发送消息，但不再加入超时管理器，达到最大发送失败次数就算了
                sendMsg()
            }
        }
    }

    fun sendMsg() {
        println("正在重发消息，message=$message")
        tcp.sendMsg(message, false)
    }

    fun getMsg(): TCPMessage {
        return message
    }

    override fun cancel() {
        task?.cancel()
        task = null
        super.cancel()
    }
}