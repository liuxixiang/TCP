package com.dushu.tcp.netty.handler

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.msg.TCPMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 5:51 下午
 * @Description: 心跳消息响应处理handler
 */
class HeartbeatRespHandler(private val tcp: TCPInterface) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val heartbeatRespMessage: TCPMessage = msg as TCPMessage
        if (heartbeatRespMessage.header == null) {
            return
        }

        val heartbeatMessage: TCPMessage? = tcp.getHeartbeatMsg()
        heartbeatMessage?.header?.let {
            if (it.messageName == heartbeatRespMessage.header.messageName) {
                println("收到服务端心跳响应消息，message=$heartbeatRespMessage")
            } else {
                // 消息透传
                ctx.fireChannelRead(msg)
            }
        }

    }
}