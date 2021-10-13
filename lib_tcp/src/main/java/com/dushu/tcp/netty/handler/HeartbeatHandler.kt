package com.dushu.tcp.netty.handler

import com.dushu.tcp.ExecutorServiceFactory
import com.dushu.tcp.TCPInterface
import com.dushu.tcp.task.HeartbeatTask
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 4:10 下午
 * @Description:心跳任务管理器
 */
class HeartbeatHandler(private val tcp: TCPInterface) : ChannelInboundHandlerAdapter() {
    private var heartbeatTask: HeartbeatTask? = null
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        super.userEventTriggered(ctx, evt)
        if (evt is IdleStateEvent) {
            when (evt.state()) {
                IdleState.READER_IDLE -> {

                    // 规定时间内没收到服务端心跳包响应，进行重连操作
                    tcp.resetConnect(false)
                }
                IdleState.WRITER_IDLE -> {
                    // 规定时间内没向服务端发送心跳包，即发送一个心跳包
                    if (heartbeatTask == null) {
                        heartbeatTask = HeartbeatTask(ctx, tcp)
                    }
                    ExecutorServiceFactory.execWorkTask(heartbeatTask)
                }
            }
        }
    }

}