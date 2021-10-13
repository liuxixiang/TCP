package com.dushu.tcp.netty

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.codec.MessageCodec
import com.dushu.tcp.codec.MessageEncoder
import com.dushu.tcp.netty.handler.HeartbeatRespHandler
import com.dushu.tcp.netty.handler.LoginAuthRespHandler
import com.dushu.tcp.netty.handler.TCPReadHandler
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 2:23 下午
 * @Description:Channel初始化配置
 */
class TCPChannelInitializer(private val client: TCPInterface) :
    ChannelInitializer<Channel>() {

    override fun initChannel(channel: Channel) {
        val pipeline: ChannelPipeline = channel.pipeline()
        // netty提供的自定义长度解码器，解决TCP拆包/粘包问题
//        pipeline.addLast("frameEncoder", LengthFieldPrepender(2))
//        pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(65535,
//            0, 2, 0, 2))
        // 增加编解码支持
        pipeline.addLast(MessageCodec())
        // 握手认证消息响应处理handler
        pipeline.addLast(LoginAuthRespHandler::class.java.simpleName,
            LoginAuthRespHandler(client))
        // 心跳消息响应处理handler
        pipeline.addLast(HeartbeatRespHandler::class.java.simpleName,
            HeartbeatRespHandler(client))
        // 接收消息处理handler
        pipeline.addLast(TCPReadHandler::class.java.simpleName, TCPReadHandler(client))
    }
}