package com.dushu.tcp.codec

import com.dushu.tcp.msg.TCPMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.MessageToByteEncoder


/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/10/9 3:33 下午
 * @Description:消息编码
 */
class MessageEncoder : MessageToByteEncoder<TCPMessage>() {
    override fun encode(ctx: ChannelHandlerContext?, message: TCPMessage?, out: ByteBuf?) {
        if (message?.header == null) {
            throw Exception("The encode message is null");
        }
    }
}