package com.dushu.tcp

import java.util.*
import kotlin.properties.Delegates

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 2:26 下午
 * @Description:配置文件
 */

class TCPConfig private constructor(val builder: Builder) {
    companion object {
        // 连接超时时长
        const val DEFAULT_CONNECT_TIMEOUT = 10 * 1000

        // 默认重连一个周期失败间隔时长
        const val DEFAULT_RECONNECT_INTERVAL_WAIT = 3 * 1000L

        // 默认一个周期重连次数
        const val DEFAULT_RECONNECT_COUNT = 3

        // 默认重连起始延时时长，重连规则：最大n次，每次延时n * 起始延时时长，重连次数达到n次后，重置
        const val DEFAULT_RECONNECT_INTERVAL_DELAY_TIME = 3 * 1000

        // 默认消息发送失败重发次数
        const val DEFAULT_RESEND_COUNT = 3

        // 默认消息重发间隔时长
        const val DEFAULT_RESEND_INTERVAL = 8 * 1000

        // 默认应用在前台时心跳消息间隔时长
        const val DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND = 3 * 1000

        // 默认应用在后台时心跳消息间隔时长
        const val DEFAULT_HEARTBEAT_INTERVAL_BACKGROUND = 30 * 1000
    }

    class Builder {
        constructor(ip: String, port: Int) {
            this.ip = ip
            this.port = port
        }

        constructor(hosts: Vector<Pair<String, Int>>) {
            this.hosts = hosts
        }

        /**
         * 服务器ip地址
         */
        lateinit var ip: String

        /**
         * 服务器端口号
         */
        var port by Delegates.notNull<Int>()

        /**
         * 服务器host 集合
         */
        lateinit var hosts: Vector<Pair<String, Int>>

        /**
         * 连接超时时长
         */
        var connectTimeout = DEFAULT_CONNECT_TIMEOUT

        /**
         * 重连间隔时长
         */
        var reconnectInterval = DEFAULT_RECONNECT_INTERVAL_DELAY_TIME

        /**
         * 应用层消息发送超时重发次数
         */
        var resendCount = DEFAULT_RESEND_COUNT

        /**
         * 应用层消息发送超时重发间隔
         */
        var resendInterval = DEFAULT_RESEND_INTERVAL


        /**
         * 应用在前台时心跳间隔时间
         */
        var foregroundHeartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND

        /**
         * 应用在后台时心跳间隔时间
         */
        var backgroundHeartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL_BACKGROUND


        /**
         * 设置服务器ip
         */
        fun setIp(ip: String): Builder {
            this.ip = ip
            return this
        }

        /**
         * 设置服务器端口号
         */
        fun setPort(port: Int): Builder {
            this.port = port
            return this
        }

        /**
         * 设置服务器hosts集合
         */
        fun setHosts(hosts: Vector<Pair<String, Int>>): Builder {
            this.hosts = hosts
            return this
        }

        /**
         * 设置连接超时时长
         */
        fun setConnectTimeout(connectTimeout: Int): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        /**
         * 设置重连间隔时长
         */
        fun setReconnectInterval(reconnectInterval: Int): Builder {
            this.reconnectInterval = reconnectInterval
            return this
        }

        /**
         * 设置应用层消息发送超时重发次数
         */
        fun setResendCount(resendCount: Int): Builder {
            this.resendCount = resendCount
            return this
        }

        /**
         * 设置应用层消息发送超时重发间隔
         */
        fun setResendInterval(resendInterval: Int): Builder {
            this.resendInterval = resendInterval
            return this
        }

        /**
         * 设置应用在前台时心跳间隔时间
         */
        fun setForegroundHeartbeatInterval(foregroundHeartbeatInterval: Int): Builder {
            this.foregroundHeartbeatInterval = foregroundHeartbeatInterval
            return this
        }

        /**
         * 设置应用在后台时心跳间隔时间
         */
        fun setBackgroundHeartbeatInterval(backgroundHeartbeatInterval: Int): Builder {
            this.backgroundHeartbeatInterval = backgroundHeartbeatInterval
            return this
        }

        fun build() = TCPConfig(this)
    }
}