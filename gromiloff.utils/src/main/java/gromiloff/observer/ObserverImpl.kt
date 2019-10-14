package gromiloff.observer

import android.util.Log

/**
 * Created by @gromiloff on 02.08.2018
 */
interface ObserverImpl {
    fun send()
}

interface ProtectedObserver : ObserverImpl {
    fun consumerClass(): String
}

open class ProtectObserverImpl(private val check: String) : ProtectedObserver {
    override fun consumerClass() = this.check

    override fun send() {
        Log.w("ProtectObserverImpl", "send $this")
        ActivityObserver.process(this)
        FragmentObserver.process(this)
        ApplicationObserver.process(this)
    }

    override fun toString(): String {
        return super.toString() + " check=$check"
    }
}

abstract class ImplementationInBackground : ObserverImpl, Runnable {
    private var thread: Thread? = null
    private var interrupt = false

    override fun send() {
        this.interrupt = false
        this.thread = Thread(this, this::class.java.simpleName)
        this.thread!!.start()
    }
}
