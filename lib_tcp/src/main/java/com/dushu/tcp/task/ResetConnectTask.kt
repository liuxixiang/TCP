package com.dushu.tcp.task

import com.dushu.tcp.annotation.CONNECT_STATE_CONNECTING
import com.dushu.tcp.annotation.CONNECT_STATE_FAILURE
import com.dushu.tcp.annotation.CONNECT_STATE_SUCCESSFUL
import com.dushu.tcp.ExecutorServiceFactory
import com.dushu.tcp.TCPConfig
import com.dushu.tcp.TCPInterface
import com.dushu.tcp.netty.NettyManager

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 5:42 下午
 * @Description:重连任务
 */
class ResetConnectTask(private val isFirst: Boolean, private val tcp: TCPInterface) : Runnable {
    private val tcpConfig: TCPConfig by lazy { tcp.getTCPConfig() }

    override fun run() {
        // 非首次进行重连，执行到这里即代表已经连接失败，回调连接状态到应用层
        if (!isFirst) {
            tcp.onConnectStatusCallback(CONNECT_STATE_FAILURE)
        }
        // 重连时，释放工作线程组，也就是停止心跳
        ExecutorServiceFactory.destroyWorkLoopGroup()
        while (!tcp.isClosed()) {
            if (!tcp.isNetworkAvailable()) {
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                continue
            }

            // 网络可用才进行连接
            var status: Int
            if (reConnect().also { status = it } == CONNECT_STATE_SUCCESSFUL) {
                tcp.onConnectStatusCallback(status)
                // 连接成功，跳出循环
                break
            }
            if (status == CONNECT_STATE_FAILURE) {
                tcp.onConnectStatusCallback(status)
                try {
                    Thread.sleep(TCPConfig.DEFAULT_RECONNECT_INTERVAL_WAIT)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 重连，首次连接也认为是第一次重连
     *
     * @return
     */
    private fun reConnect(): Int {
        // 未关闭才去连接
        if (!tcp.isClosed()) {
            NettyManager.INSTANCE.initBootstrap(tcp)
            return connectServer()
        }
        return CONNECT_STATE_FAILURE
    }

    /**
     * 连接服务器
     *
     * @return
     */
    private fun connectServer(): Int {
        // 如果服务器地址无效，直接回调连接状态，不再进行连接
        // 有效的服务器地址示例：127.0.0.1 8860
        val serverUrlList = tcpConfig.builder.hosts
        if (serverUrlList.isNullOrEmpty()) {
            return CONNECT_STATE_FAILURE
        }
        var i = 0
        while (!tcp.isClosed() && i < serverUrlList.size) {
            val serverUrl = serverUrlList[i]
            // 如果服务器地址无效，直接回调连接状态，不再进行连接
            if (serverUrl.first.isEmpty()) {
                return CONNECT_STATE_FAILURE
            }
            for (j in 1..TCPConfig.DEFAULT_RECONNECT_COUNT) {
                // 如果已关闭，或网络不可用，直接回调连接状态，不再进行连接
                if (tcp.isClosed() || !tcp.isNetworkAvailable()) {
                    return CONNECT_STATE_FAILURE
                }
                // 回调连接状态
                if (tcp.getConnectStatus() != CONNECT_STATE_CONNECTING) {
                    tcp.onConnectStatusCallback(CONNECT_STATE_CONNECTING)
                }
                println(String.format("正在进行『%s』的第『%d』次连接，当前重连延时时长为『%dms』",
                    serverUrl,
                    j,
                    j * tcpConfig.builder.reconnectInterval))
                try {
                    tcpConfig.builder.ip = serverUrl.first // 获取host
                    tcpConfig.builder.port = serverUrl.second//获取port
                    val channel = NettyManager.INSTANCE.toServer(tcpConfig.builder.ip,
                        tcpConfig.builder.port) // 连接服务器
                    // channel不为空，即认为连接已成功
                    if (channel != null) {
                        return CONNECT_STATE_SUCCESSFUL
                    } else {
                        // 连接失败，则线程休眠n * 重连间隔时长
                        Thread.sleep((j * tcpConfig.builder.reconnectInterval).toLong())
                    }
                } catch (e: InterruptedException) {
                    tcp.close()
                    break // 线程被中断，则强制关闭
                }
            }
            i++
        }

        // 执行到这里，代表连接失败
        return CONNECT_STATE_FAILURE
    }


}