package com.dushu.tcp.netty.handler

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.msg.TCPMessage
import com.dushu.tcp.netty.NettyManager
import com.dushu.tcp.util.GsonUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.lang.Exception

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 2:35 下午
 * @Description:握手认证消息响应处理handler
 */
class LoginAuthRespHandler(private val tcp: TCPInterface) :
    ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val handshakeRespMessage: TCPMessage = msg as TCPMessage
        if (handshakeRespMessage.header == null) {
            return
        }
        val handshakeMessage: TCPMessage? = tcp.getHandshakeMsg()
        if (handshakeMessage?.header == null) {
            return
        }
        val handshakeMessageName = handshakeMessage.header.messageName
        if (handshakeMessageName == handshakeRespMessage.header.messageName) {
            println("收到服务端握手响应消息，message=$handshakeRespMessage")
            var resultCode = ""
            try {
                val map = GsonUtil.GsonToMaps<Any?>(handshakeRespMessage.content)
                 resultCode = (map as MutableMap<*, *>)["resultCode"].toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if ("0" == resultCode) {
                    // 握手成功，马上先发送一条心跳消息，至于心跳机制管理，交由HeartbeatHandler
                    val heartbeatMessage: TCPMessage? = tcp.getHeartbeatMsg()
                    heartbeatMessage?.let {
                        // 握手成功，检查消息发送超时管理器里是否有发送超时的消息，如果有，则全部重发
                        tcp.getMsgTimeoutTimerManager()?.sendMsgAll()
                        println("发送心跳消息：${heartbeatMessage}当前心跳间隔为：${tcp.getHeartbeatInterval()}ms".trimIndent())
                        tcp.sendMsg(heartbeatMessage)
                    }
                    // 添加心跳消息管理handler
                    NettyManager.INSTANCE.addHeartbeatHandler(tcp)

                } else {
                    //imsClient.resetConnect(false);// 握手失败，触发重连
                    //握手失败且返回了消息一定是服务端认证没通过 所以这里需要关闭客户端
                    tcp.close()
                }
            }
        } else {
            // 消息透传
            ctx.fireChannelRead(msg)
        }
    }
}