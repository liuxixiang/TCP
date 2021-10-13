package com.dushu.tcp.msg

import com.dushu.tcp.codec.ICodec
import io.netty.buffer.ByteBuf

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/10/11 1:38 下午
 * @Description:消息头部分
 */
class TCPMessageHead : ICodec {
    /**
     * 消息系列
     */
    var seq: Int = 1

    /**
     * 消息名
     */
    var messageName: String? = null

    /**
     * 版本号
     */
    var version: Int = 1

    /**
     * 消息体类型
     */
    var contentType: Int = 0

    companion object {
        private const val MAX_MESSAGE_NAME_SIZE = 256
    }

    override fun encode(byteBuf: ByteBuf) {
        val headStartIndex = byteBuf.writerIndex()
        byteBuf.writerIndex(headStartIndex + 2)
        writeUtf8String(byteBuf, messageName, MAX_MESSAGE_NAME_SIZE)
        byteBuf.writeInt(seq)
        byteBuf.writeShort(version)
        byteBuf.writeByte(contentType)
        val headEndIndex = byteBuf.writerIndex()
        val headSize = byteBuf.writerIndex() - headStartIndex
        byteBuf.writerIndex(headStartIndex)
        byteBuf.writeShort(headSize)
        byteBuf.writerIndex(headEndIndex)
    }

    override fun decode(byteBuf: ByteBuf) {
        val headSize = byteBuf.readShort().toInt()
        messageName = readUtf8String(byteBuf, MAX_MESSAGE_NAME_SIZE)
        seq = byteBuf.readInt()
        version = byteBuf.readShort().toInt()
        contentType = byteBuf.readByte().toInt()
    }

    override fun toString(): String {
        return "TCPMessageHead(seq=$seq, messageName=$messageName, version=$version, contentType=$contentType)"
    }

}