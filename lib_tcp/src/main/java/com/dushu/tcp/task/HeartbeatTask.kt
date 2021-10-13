package com.dushu.tcp.task

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.msg.TCPMessage
import io.netty.channel.ChannelHandlerContext

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 5:23 下午
 * @Description:心态任务
 */
class HeartbeatTask(private val ctx: ChannelHandlerContext, private val tcp: TCPInterface) :
    Runnable {
    override fun run() {
        if (ctx.channel().isActive) {
            val heartbeatMessage: TCPMessage = tcp.getHeartbeatMsg() ?: return
            println("发送心跳消息，message=${heartbeatMessage}当前心跳间隔为：${tcp.getHeartbeatInterval()}ms".trimIndent())
            tcp.sendMsg(heartbeatMessage, false)
        }
    }
}