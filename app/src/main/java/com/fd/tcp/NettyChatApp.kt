package com.fd.tcp

import android.app.Application

/**
 * @Author: liuxihui
 * @Email: liuxihui@dushu365.com
 * @CreateDate: 2021/9/28 9:36 上午
 * @Description:
 */
class NettyChatApp : Application() {
    companion object {
        private var instance: NettyChatApp? = null

        fun sharedInstance(): NettyChatApp {
            checkNotNull(instance) { "app not init..." }
            return instance!!
        }

    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}