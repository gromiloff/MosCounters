package gromiloff.observer

import android.util.Log

/**
 * Created by @gromiloff on 31.07.2018
 */
class FragmentObserver : FastObserver() {
    companion object {
        private val instance = FragmentObserver()
        fun process(obj: ObserverImpl) {
            Log.w("FragmentObserver", "processed $obj")
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

open class FragmentObserverImpl : ObserverImpl {
    override fun send() {
        FragmentObserver.process(this)
    }
}