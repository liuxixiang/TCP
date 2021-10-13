package com.dushu.tcp.netty.handler

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.msg.TCPMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 5:28 下午
 * @Description:消息接收处理handler
 */
class TCPReadHandler(private val tcp: TCPInterface) : ChannelInboundHandlerAdapter() {
    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        System.err.println("TCPReadHandler channelInactive()")
        ctx.channel()?.let {
            it.close()
            ctx.close()
        }
        // 触发重连
        tcp.resetConnect(false)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        System.err.println("TCPReadHandler exceptionCaught()")
        ctx.channel()?.let {
            it.close()
            ctx.close()
        }

        // 触发重连
        tcp.resetConnect(false)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val message: TCPMessage = msg as TCPMessage
        if (message.header == null) {
            return
        }


//        val msgType: Int = message.header.messageName
//        if (msgType == tcp.getServerSentReportMsgType()) {
//            val statusReport: Int = message.head.statusReport
//            println(String.format("服务端状态报告：「%d」, 1代表成功，0代表失败", statusReport))
//            if (statusReport == .DEFAULT_REPORT_SERVER_SEND_MSG_SUCCESSFUL) {
//                println("收到服务端消息发送状态报告，message=$message，从超时管理器移除")
//                tcp.getMsgTimeoutTimerManager()?.remove(message.head.msgId)
//            }
//        } else {
//            // 其它消息
//            // 收到消息后，立马给服务端回一条消息接收状态报告
//            println("收到消息，message=$message")
//            val receivedReportMsg: MessageProtobuf.Msg? = buildReceivedReportMsg(message.head.msgId)
//            receivedReportMsg?.let {
//                tcp.sendMsg(receivedReportMsg)
//            }
//        }
        // 接收消息，由消息转发器转发到应用层
        tcp.getMsgDispatcher()?.receivedMsg(message)
    }
}