package com.dushu.tcp.app

import com.dushu.tcp.TCPInterface
import com.dushu.tcp.netty.NettyTCP

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 10:29 上午
 * @Description:tcp实例工厂方法
 */
class TCPFactory {
    companion object {
        fun getNettyTCP(): TCPInterface {
            return NettyTCP.INSTANCE
        }
    }
}