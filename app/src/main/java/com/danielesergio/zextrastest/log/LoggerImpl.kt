package com.danielesergio.zextrastest.log

object LoggerImpl: Logger {

    const val PREPEND_MESSAGE = "Log:"
    var delegate: Logger? = null

    override fun d(tag:String, message: String) {
        delegate?.d(tag,"$PREPEND_MESSAGE $message")
    }

    override fun i(tag:String,  message: String) {
        delegate?.i(tag,"$PREPEND_MESSAGE $message")
    }

    override fun w(tag:String,  message: String, throwable: Throwable?) {
        delegate?.w(tag, "$PREPEND_MESSAGE $message", throwable)
    }

    override fun e(tag:String, message: String, throwable: Throwable?) {
        delegate?.e(tag, "$PREPEND_MESSAGE $message", throwable)
    }

    fun <T>startEndMethod(tag: String, methodName:String, action: () -> T): T{
        d(tag, "$methodName start")
        return action().also {
            d(tag, "$methodName end")
        }
    }
}