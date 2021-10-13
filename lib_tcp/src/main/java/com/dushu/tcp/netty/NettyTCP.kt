package com.dushu.tcp.netty

import com.dushu.tcp.*
import com.dushu.tcp.annotation.*
import com.dushu.tcp.task.ResetConnectTask
import com.dushu.tcp.msg.MsgDispatcher
import com.dushu.tcp.msg.MsgTimeoutTimerManager
import com.dushu.tcp.msg.TCPMessage

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 2:27 下午
 * @Description:NettyTcp的实现
 */
class NettyTCP private constructor() : TCPInterface {

    companion object {
        val INSTANCE: NettyTCP by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NettyTCP()
        }
    }

    private var isClosed = false // 标识tcp是否已关闭
    private lateinit var tcpConfig: TCPConfig // 标识tcp是否已关闭
    private var onTCPEventListener: OnTCPEventListener? = null  // 与应用层交互的Listener
    private var connectStatusCallback: ConnectStatusCallback? = null // 连接状态回调监听器
    private var msgDispatcher: MsgDispatcher? = null// 消息转发器
    private var msgTimeoutTimerManager: MsgTimeoutTimerManager? = null // 消息发送超时定时器管理

    // 心跳间隔时间
    private var heartbeatInterval: Int = TCPConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND

    @ConnectStatus
    private var connectStatus: Int = CONNECT_STATE_FAILURE // 连接状态，初始化为连接失败

    override fun init(
        config: TCPConfig,
        callback: ConnectStatusCallback?,
        onTCPEventListener: OnTCPEventListener?,
    ) {
        close()
        isClosed = false
        this.tcpConfig = config
        this.connectStatusCallback = callback
        this.onTCPEventListener = onTCPEventListener
        msgDispatcher = MsgDispatcher(onTCPEventListener)
        msgTimeoutTimerManager = MsgTimeoutTimerManager(this)
        resetConnect(true) // 进行第一次连接
    }

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     */
    override fun resetConnect() {
        this.resetConnect(false);
    }

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     * 重载
     *
     * @param isFirst 是否首次连接
     */
    override fun resetConnect(isFirst: Boolean) {
        if (!isFirst) {
            try {
                Thread.sleep(TCPConfig.DEFAULT_RECONNECT_INTERVAL_WAIT)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        if (!isClosed && connectStatus != CONNECT_STATE_CONNECTING) {
            synchronized(this) {
                if (!isClosed && connectStatus != CONNECT_STATE_CONNECTING) {
                    // 回调连接状态
                    onConnectStatusCallback(CONNECT_STATE_CONNECTING)
                    // 先关闭channel
                    NettyManager.INSTANCE.closeChannel()
                    // 执行重连任务
                    ExecutorServiceFactory.execBossTask(ResetConnectTask(isFirst, this))
                }
            }
        }
    }

    /**
     * 关闭连接，同时释放资源
     */
    override fun close() {
        if (isClosed) {
            return
        }
        isClosed = true
        // 释放线程池
        ExecutorServiceFactory.destroy()
        NettyManager.INSTANCE.close()
    }

    /**
     * 标识是否已关闭
     *
     * @return
     */
    override fun isClosed(): Boolean = isClosed

    /**
     * 发送消息
     *
     * @param message
     */
    override fun sendMsg(message: TCPMessage?) {
        this.sendMsg(message, true)
    }

    override fun sendMsg(message: TCPMessage?, isJoinTimeoutManager: Boolean) {
        if (message?.header == null) {
            println("发送消息失败，消息为空\tmessage=$message")
            return
        }
        if (message.header.messageName?.isNotEmpty() == true && isJoinTimeoutManager) {
            msgTimeoutTimerManager?.add(message)
        }
        NettyManager.INSTANCE.sendMsg(message)
    }

    override fun getTCPConfig(): TCPConfig {
        return tcpConfig
    }


    /**
     * 设置app前后台状态
     *
     * @param appStatus
     */
    override fun setAppStatus(appStatus: Int) {
        heartbeatInterval = if (appStatus == APP_STATUS_BACKGROUND) {
            tcpConfig.builder.backgroundHeartbeatInterval
        } else {
            tcpConfig.builder.foregroundHeartbeatInterval
        }

        NettyManager.INSTANCE.addHeartbeatHandler(this)
    }

    /**
     * 获取心跳间隔时间
     *
     * @return
     */
    override fun getHeartbeatInterval(): Int = heartbeatInterval

    /**
     * 获取由应用层构造的握手消息
     *
     * @return
     */
    override fun getHandshakeMsg(): TCPMessage? =
        onTCPEventListener?.getHandshakeMsg()

    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    override fun getHeartbeatMsg(): TCPMessage? =
        onTCPEventListener?.getHeartbeatMsg()

    override fun getClientReceivedReportMsgType(): String? =
        onTCPEventListener?.getClientReceivedReportMsgType()


    /**
     * 获取消息转发器
     *
     * @return
     */
    override fun getMsgDispatcher(): MsgDispatcher? = msgDispatcher

    /**
     * 获取消息发送超时定时器
     *
     * @return
     */
    override fun getMsgTimeoutTimerManager(): MsgTimeoutTimerManager? = msgTimeoutTimerManager

    /**
     * 回调连接状态
     *
     * @param connectStatus
     */
    override fun onConnectStatusCallback(@ConnectStatus connectStatus: Int) {
        this.connectStatus = connectStatus
        when (connectStatus) {
            CONNECT_STATE_CONNECTING -> {
                println("连接中...")
                connectStatusCallback?.onConnecting()
            }
            CONNECT_STATE_SUCCESSFUL -> {
                println(String.format("连接成功，host『%s』, port『%s』",
                    getTCPConfig().builder.ip,
                    getTCPConfig().builder.port))
                connectStatusCallback?.onConnected()
                // 连接成功，发送握手消息
                getHeartbeatMsg()?.let {
                    println("发送心跳消息，message=$it")
                    sendMsg(it, false)
                } ?: System.err.println("请应用层构建握手消息！")
            }
            CONNECT_STATE_FAILURE -> {
                println("连接失败")
                connectStatusCallback?.onConnectFailed()
            }
            else -> {
                println("连接失败")
                connectStatusCallback?.onConnectFailed()
            }
        }
    }

    @ConnectStatus
    override fun getConnectStatus(): Int {
        return connectStatus
    }

    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    override fun isNetworkAvailable(): Boolean {
        return onTCPEventListener?.isNetworkAvailable() ?: false
    }


}