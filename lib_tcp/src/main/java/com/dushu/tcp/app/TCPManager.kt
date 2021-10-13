package com.dushu.tcp.app

import com.dushu.tcp.OnTCPEventListener
import com.dushu.tcp.TCPConfig
import com.dushu.tcp.TCPInterface
import com.dushu.tcp.msg.MessageProcessor
import com.dushu.tcp.msg.TCPMessage
import com.dushu.tcp.msg.TCPMessageHead
import com.dushu.tcp.util.GsonUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/29 3:37 下午
 * @Description:TCP
 */
class TCPManager private constructor() {
    private var tcp: TCPInterface? = null
    private var isActive = false

    companion object {
        val INSTANCE: TCPManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TCPManager()
        }

    }

    @Synchronized
    fun init(userId: String?, token: String?, hosts: String, appStatus: Int) {
        if (!isActive()) {
            val serverUrlList = convertHosts(hosts)
            if (serverUrlList == null || serverUrlList.size == 0) {
                println("init IMLibClientBootstrap error,ims hosts is null")
                return
            }
            isActive = true
            println("init IMLibClientBootstrap, servers=$hosts")
            tcp?.close()
            tcp = TCPFactory.getNettyTCP()
            tcp?.init(TCPConfig.Builder(serverUrlList).build(), null, object : OnTCPEventListener {
                override fun dispatchMsg(message: TCPMessage?) {
                    MessageProcessor.INSTANCE
                        .receiveMsg(message)
                }

                override fun isNetworkAvailable(): Boolean {
                    return true
                }

                override fun getHandshakeMsg(): TCPMessage {
                    val head = TCPMessageHead()
                    head.messageName = "_MS:AuthReq"
                    head.version = 5
                    head.contentType = 0
                    head.seq = 1
                    val message = TCPMessage(head)
                    message.content = GsonUtil.GsonString(mapOf("token" to "lxh"))
                    return message
                }

                override fun getHeartbeatMsg(): TCPMessage {
                    val head = TCPMessageHead()
                    head.messageName = "_MS:Ping"
                    head.contentType = 0
                    val message = TCPMessage(head)
                    message.content =
                        GsonUtil.GsonString(mapOf("timestamp" to System.currentTimeMillis()))
                    return message
                }

                override fun getClientReceivedReportMsgType(): String {
                    return ""
                }

            })
        }
        updateAppStatus(appStatus)
    }

    fun isActive(): Boolean {
        return isActive
    }

    /**
     * 发送消息
     *
     * @param message
     */
    fun sendMessage(message: TCPMessage?) {
        if (isActive) {
            tcp?.sendMsg(message)
        }
    }

    private fun convertHosts(hosts: String?): Vector<Pair<String, Int>>? {
        if (!hosts.isNullOrEmpty()) {
            val hostArray = GsonUtil.GsonToListMaps<Any>(hosts)
            if (hostArray.size > 0) {
                val serverUrlList = Vector<Pair<String, Int>>()
                for (json in hostArray) {
                    serverUrlList.add(json["host"].toString() to json["port"].toString().toInt())
                }

                return serverUrlList
            }
        }
        return null
    }

    fun updateAppStatus(appStatus: Int) {
        tcp?.setAppStatus(appStatus)
    }

}