package com.dushu.tcp.msg


/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/28 10:05 上午
 * @Description:消息处理器接口
 */
interface IMessageProcessor {
    fun receiveMsg(message: TCPMessage?)
    fun sendMsg(message: TCPMessage?)
}