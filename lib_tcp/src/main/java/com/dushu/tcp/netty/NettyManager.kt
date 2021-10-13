package com.dushu.tcp.netty

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.netty.handler.HeartbeatHandler
import com.dushu.tcp.netty.handler.TCPReadHandler
import com.dushu.tcp.msg.TCPMessage
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/30 1:51 下午
 * @Description:Netty api
 */
class NettyManager private constructor() {
    private var bootstrap: Bootstrap? = null
    private var channel: Channel? = null

    companion object {
        val INSTANCE: NettyManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NettyManager()
        }
    }


    /**
     * 初始化bootstrap
     */
    fun initBootstrap(tcp: TCPInterface) {
        try {
            // 先释放EventLoop线程组
            bootstrap?.group()?.shutdownGracefully()
        } finally {
            bootstrap = null
        }
        // 初始化bootstrap
        bootstrap = Bootstrap().let {
            it.group(NioEventLoopGroup(4)).channel(NioSocketChannel::class.java)
            // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
            it.option(ChannelOption.SO_KEEPALIVE, true)
            // 设置禁用nagle算法
            it.option(ChannelOption.TCP_NODELAY, true)
            // 设置连接超时时长
            it.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                tcp.getTCPConfig().builder.connectTimeout)
            // 设置初始化Channel
            it.handler(TCPChannelInitializer(tcp))
        }
    }


    /**
     * 真正连接服务器的地方
     */
    fun toServer(currentHost: String, currentPort: Int): Channel? {
        channel = try {
            bootstrap?.connect(currentHost, currentPort)?.sync()?.channel()
        } catch (e: Exception) {
            try {
                Thread.sleep(500)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
            System.err.println(String.format("连接Server(ip[%s], port[%s])失败",
                currentHost,
                currentPort))
            null
        }
        return channel
    }

    /**
     * 发送消息
     */
    fun sendMsg(message: TCPMessage?) {
        channel?.let {
            try {
                it.writeAndFlush(message)
            } catch (ex: Exception) {
                println("发送消息失败，reason:" + ex.message + "\tmessage=" + message)
            }
        } ?: println("发送消息失败，channel为空\tmessage=$message")
    }

    /**
     * 关闭channel
     */
    fun closeChannel() {
        try {
            channel?.let {
                try {
                    removeHandler(HeartbeatHandler::class.java.simpleName)
                    removeHandler(TCPReadHandler::class.java.getSimpleName())
                    removeHandler(IdleStateHandler::class.java.simpleName)
                } finally {
                    try {
                        it.close()
                    } catch (ex: Exception) {
                    }
                    try {
                        it.eventLoop().shutdownGracefully()
                    } catch (ex: Exception) {
                    }
                    channel = null
                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("关闭channel出错，reason:" + ex.message)
        }
    }

    /**
     * 关闭连接，同时释放资源
     */
    fun close() {
        // 关闭channel
        try {
            closeChannel()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        // 关闭bootstrap
        try {
            if (bootstrap != null) {
                bootstrap!!.group().shutdownGracefully()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        channel = null
        bootstrap = null
    }

    /**
     * 添加心跳消息管理handler
     */
    fun addHeartbeatHandler(tcp: TCPInterface) {
        channel?.let {
            if (!it.isActive || it.pipeline() == null) {
                return
            }
            try {
                // 之前存在的读写超时handler，先移除掉，再重新添加
                if (it.pipeline()[IdleStateHandler::class.java.simpleName] != null) {
                    it.pipeline().remove(IdleStateHandler::class.java.simpleName)
                }
                // 3次心跳没响应，代表连接已断开
                it.pipeline().addFirst(IdleStateHandler::class.java.simpleName, IdleStateHandler(
                    (tcp.getHeartbeatInterval() * 3).toLong(),
                    tcp.getHeartbeatInterval().toLong(),
                    0,
                    TimeUnit.MILLISECONDS))

                // 重新添加HeartbeatHandler
                if (it.pipeline()[HeartbeatHandler::class.java.simpleName] != null) {
                    it.pipeline().remove(HeartbeatHandler::class.java.simpleName)
                }
                if (it.pipeline()[TCPReadHandler::class.java.simpleName] != null) {
                    it.pipeline().addBefore(TCPReadHandler::class.java.simpleName,
                        HeartbeatHandler::class.java.simpleName,
                        HeartbeatHandler(tcp))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                System.err.println("添加心跳消息管理handler失败，reason：" + e.message)
            }
        }


    }


    /**
     * 移除指定handler
     *
     * @param handlerName
     */
    private fun removeHandler(handlerName: String) {
        try {
            channel?.let {
                if (it.pipeline()[handlerName] != null) {
                    it.pipeline().remove(handlerName)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("移除handler失败，handlerName=$handlerName")
        }
    }

}