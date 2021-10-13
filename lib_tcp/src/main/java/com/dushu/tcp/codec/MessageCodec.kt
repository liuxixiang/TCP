package com.dushu.tcp.codec

import com.dushu.tcp.msg.TCPMessage
import com.dushu.tcp.msg.TCPMessageHead
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/10/9 3:36 下午
 * @Description:消息编码解码
 */

class MessageCodec : ByteToMessageCodec<TCPMessage>() {
    override fun encode(ctx: ChannelHandlerContext, msg: TCPMessage?, byteBuf: ByteBuf) {
        val startIndex: Int = byteBuf.writerIndex()
        //头部长度字段
        val headLengthIndex = startIndex + 4

        byteBuf.writerIndex(headLengthIndex)

        msg?.header?.encode(byteBuf)
        msg?.encode(byteBuf)
        val packetSize: Int = byteBuf.writerIndex() - headLengthIndex

        val endIndex: Int = byteBuf.writerIndex()
        byteBuf.writerIndex(startIndex)
        byteBuf.writeInt(packetSize)
        byteBuf.writerIndex(endIndex)
        println(ByteBufUtil.hexDump(byteBuf))

    }

    override fun decode(ctx: ChannelHandlerContext?, byteBuf: ByteBuf, out: MutableList<Any>?) {
        val dataSize: Int = byteBuf.readableBytes()
        if (dataSize < 4) {
            return
        }
        val startIndex: Int = byteBuf.readerIndex()
        val packetSize: Int = byteBuf.readInt()

        if (packetSize > byteBuf.readableBytes()) {
            byteBuf.readerIndex(startIndex)
            return
        }
        val header = TCPMessageHead()
        header.decode(byteBuf)
        val jsonPacket = TCPMessage(header)
        jsonPacket.decode(byteBuf)
        out?.add(jsonPacket)
    }

}