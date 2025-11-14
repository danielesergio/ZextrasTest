package com.danielesergio.zextrastest.log

object LoggerImpl: Logger {

    var delegate: Logger? = null

    override fun d(tag:String, message: String) {
        delegate?.d(tag,message)
    }

    override fun i(tag:String,  message: String) {
        delegate?.i(tag,message)
    }

    override fun w(tag:String,  message: String, throwable: Throwable?) {
        delegate?.w(tag, message, throwable)
    }

    override fun e(tag:String, message: String, throwable: Throwable?) {
        delegate?.e(tag, message, throwable)
    }
}