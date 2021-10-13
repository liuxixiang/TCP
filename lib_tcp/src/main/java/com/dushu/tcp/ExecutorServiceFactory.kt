package com.dushu.tcp

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/27 10:53 上午
 * @Description:线程池工厂，负责重连和心跳线程调度
 */
object ExecutorServiceFactory {
    private var bossPool: ExecutorService? = null// 管理线程组，负责重连
    private var workPool: ExecutorService? = null // 工作线程组，负责心跳

    /**
     * 初始化boss线程池
     */
    @Synchronized
    fun initBossLoopGroup() {
        initBossLoopGroup(1)
    }

    /**
     * 初始化boss线程池
     * 重载
     *
     * @param size 线程池大小
     */
    @Synchronized
    fun initBossLoopGroup(size: Int) {
        destroyBossLoopGroup()
        bossPool = Executors.newFixedThreadPool(size)
    }

    /**
     * 初始化work线程池
     */
    @Synchronized
    fun initWorkLoopGroup() {
        initWorkLoopGroup(1)
    }

    /**
     * 初始化work线程池
     * 重载
     *
     * @param size 线程池大小
     */
    @Synchronized
    fun initWorkLoopGroup(size: Int) {
        destroyWorkLoopGroup()
        workPool = Executors.newFixedThreadPool(size)
    }

    /**
     * 执行boss任务
     *
     * @param r
     */
    fun execBossTask(r: Runnable?) {
        if (bossPool == null) {
            initBossLoopGroup()
        }
        bossPool!!.execute(r)
    }

    /**
     * 执行work任务
     *
     * @param r
     */
    fun execWorkTask(r: Runnable?) {
        if (workPool == null) {
            initWorkLoopGroup()
        }
        workPool!!.execute(r)
    }

    /**
     * 释放boss线程池
     */
    @Synchronized
    fun destroyBossLoopGroup() {
        if (bossPool != null) {
            try {
                bossPool!!.shutdownNow()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                bossPool = null
            }
        }
    }

    /**
     * 释放work线程池
     */
    @Synchronized
    fun destroyWorkLoopGroup() {
        if (workPool != null) {
            try {
                workPool!!.shutdownNow()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                workPool = null
            }
        }
    }

    /**
     * 释放所有线程池
     */
    @Synchronized
    fun destroy() {
        destroyBossLoopGroup()
        destroyWorkLoopGroup()
    }
}