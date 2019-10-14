package gromiloff.observer.impl

import android.app.Application
import android.content.Context
import gromiloff.observer.ApplicationObserver
import gromiloff.observer.FastObserver
import gromiloff.observer.ProtectedObserverListener

open class ObserverApplication : Application(), ProtectedObserverListener {

    override fun update(o: FastObserver, arg: Any?) {}
    override fun consumerClass() = toString()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ApplicationObserver.addObserver(this)
    }
}