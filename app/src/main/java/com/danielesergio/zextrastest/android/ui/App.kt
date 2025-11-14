package com.danielesergio.zextrastest.android.ui

import android.app.Application
import android.util.Log
import com.danielesergio.zextrastest.BuildConfig
import com.danielesergio.zextrastest.log.Logger
import com.danielesergio.zextrastest.log.LoggerImpl

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Plant a DebugTree only for debug builds
        if (BuildConfig.DEBUG) {
            LoggerImpl.delegate = TimberLogger()
        }
    }

    private class TimberLogger: Logger{
        override fun d(tag:String, message: String) {
            Log.d(tag, message)
        }

        override fun i(tag:String,message: String) {
            Log.i(tag, message)
        }

        override fun w(tag:String, message: String, throwable: Throwable?) {
            Log.w(tag, message, throwable)
        }

        override fun e(tag:String, message: String, throwable: Throwable?) {
            Log.e(tag, message, throwable)
        }

    }

}