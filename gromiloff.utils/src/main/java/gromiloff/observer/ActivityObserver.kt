package gromiloff.observer

import android.util.Log

/**
 * Created by @gromiloff on 31.07.2018
 */
class ActivityObserver : FastObserver() {
    companion object {
        private val instance = ActivityObserver()
        fun process(obj: ObserverImpl) {
            Log.w("ActivityObserver", "processed $obj")
            this.instance.notifyObservers(obj)
        }

        fun addObserver(o: ProtectedObserverListener) {
            this.instance.addObserver(o)
        }

        fun deleteObserver(o: ProtectedObserverListener) {
            this.instance.deleteObserver(o)
        }
    }
}

open class ActivityObserverImpl : ObserverImpl {
    override fun send() {
        ActivityObserver.process(this)
    }
}

open class ActivityAndFragmentObserverImpl : FragmentObserverImpl() {
    override fun send() {
        super.send()
        ActivityObserver.process(this)
    }
}
