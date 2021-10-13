package com.dushu.tcp

import com.dushu.tcp.annotation.ConnectStatus
import com.dushu.tcp.msg.MsgDispatcher
import com.dushu.tcp.msg.MsgTimeoutTimerManager
import com.dushu.tcp.msg.TCPMessage
import java.util.*

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 2:41 下午
 * @Description:抽象接口，需要切换到其它方式实现，实现此接口即可
 */
interface TCPInterface {
    /**
     * 初始化
     *
     * @param serverUrlList 服务器地址列表
     * @param listenerKeepAlive      与应用层交互的listener
     * @param callback      连接状态回调
     */
    fun init(
        config: TCPConfig,
        callback: ConnectStatusCallback?,
        onTCPEventListener: OnTCPEventListener?,
    )

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     */
    fun resetConnect()


    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     * 重载
     *
     * @param isFirst 是否首次连接
     */
    fun resetConnect(isFirst: Boolean)

    /**
     * 关闭连接，同时释放资源
     */
    fun close()

    /**
     * 标识ims是否已关闭
     *
     * @return
     */
    fun isClosed(): Boolean

    /**
     * 发送消息
     *
     * @param message
     */
    fun sendMsg(message: TCPMessage?)

    /**
     * 发送消息
     * 重载
     *
     * @param message
     * @param isJoinTimeoutManager 是否加入发送超时管理器
     */
    fun sendMsg(message: TCPMessage?, isJoinTimeoutManager: Boolean)

    /**
     * 获取tcp 配置
     *
     * @return
     */
    fun getTCPConfig(): TCPConfig


    /**
     * 设置app前后台状态
     *
     * @param appStatus
     */
    fun setAppStatus(appStatus: Int)

    /**
     * 获取心跳间隔时间
     *
     * @return
     */
    fun getHeartbeatInterval(): Int

    /**
     * 获取由应用层构造的握手消息
     *
     * @return
     */
    fun getHandshakeMsg(): TCPMessage?

    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    fun getHeartbeatMsg(): TCPMessage?

    /**
     * 获取应用层消息接收状态报告消息类型
     *
     * @return
     */
    fun getClientReceivedReportMsgType(): String?

    /**
     * 获取消息转发器
     *
     * @return
     */
    fun getMsgDispatcher(): MsgDispatcher?

    /**
     * 获取消息发送超时定时器
     *
     * @return
     */
    fun getMsgTimeoutTimerManager(): MsgTimeoutTimerManager?

    /**
     * 回调连接状态
     *
     * @param connectStatus
     */
    fun onConnectStatusCallback(@ConnectStatus connectStatus: Int)

    /**
     * 回调连接状态
     *
     * @return connectStatus
     */

    @ConnectStatus
    fun getConnectStatus(): Int


    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    fun isNetworkAvailable(): Boolean
}