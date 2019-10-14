package gromiloff.observer

import android.util.Log

/**
 * Created by @gromiloff on 31.07.2018
 */
class ApplicationObserver : FastObserver() {
    companion object {
        private val instance = ApplicationObserver()

        fun process(obj: ObserverImpl) {
            Log.w("ApplicationObserver", "processed $obj")
            instance.notifyObservers(obj)
        }

        fun addObserver(o: ProtectedObserverListener) {
            instance.addObserver(o)
        }

        fun deleteObserver(o: ProtectedObserverListener) {
            instance.deleteObserver(o)
        }
    }
}

open class ApplicationObserverImpl : ObserverImpl {
    override fun send() {
        ApplicationObserver.process(this)
    }
}

open class ActivityAndApplicationObserverImpl : ActivityObserverImpl() {
    override fun send() {
        super.send()
        ApplicationObserver.process(this)
    }
}

open class FullObserverImpl : ActivityAndFragmentObserverImpl() {
    override fun send() {
        super.send()
        ApplicationObserver.process(this)
    }
}
