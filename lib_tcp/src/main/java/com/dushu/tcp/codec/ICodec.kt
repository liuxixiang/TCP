package com.dushu.tcp.codec

import android.R.attr
import java.io.Serializable
import io.netty.buffer.ByteBuf;
import android.R.attr.maxLength
import java.nio.charset.StandardCharsets
import android.R.attr.maxLength


/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/10/9 5:45 下午
 * @Description:编码接口
 */
interface ICodec : Serializable {
    fun encode(byteBuf: ByteBuf)
    fun decode(byteBuf: ByteBuf)
    fun readUtf8String(byteBuf: ByteBuf, maxLength: Int): String {
        val length = byteBuf.readInt()
        if (length > maxLength) {
            throw Exception("读取字符串数据超过长度限制:" + attr.maxLength, null)
        }
        val data = ByteArray(length)
        byteBuf.readBytes(data)
        return String(data, StandardCharsets.UTF_8)
    }

    fun writeUtf8String(byteBuf: ByteBuf, s: String?, maxLength: Int) {
        if (s == null) {
            byteBuf.writeInt(0)
        } else {
            val data: ByteArray = s.toByteArray(StandardCharsets.UTF_8)
            if (data.size > attr.maxLength) {
                throw Exception("写入字符串数据超过长度限制:" + attr.maxLength + ",s:" + s, null)
            }
            byteBuf.writeInt(data.size)
            byteBuf.writeBytes(data)
        }

    }
}