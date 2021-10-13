package com.dushu.tcp.msg

import com.dushu.tcp.codec.ICodec
import io.netty.buffer.ByteBuf

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 3:07 下午
 * @Description:消息包主体
 */
class TCPMessage(val header: TCPMessageHead?) : ICodec {
    var content: String? = null

    companion object {
        private const val MAX_BODY_SIZE = 64 * 1024
    }

    override fun encode(byteBuf: ByteBuf) {
        writeUtf8String(byteBuf, content, MAX_BODY_SIZE)
    }

    override fun decode(byteBuf: ByteBuf) {
        content = readUtf8String(byteBuf, MAX_BODY_SIZE)
    }

    override fun toString(): String {
        return "TCPMessage(header=$header, content=$content)"
    }

}



