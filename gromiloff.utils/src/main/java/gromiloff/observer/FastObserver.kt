package gromiloff.observer

import android.util.Log
import java.util.*

/**
 * Created by @gromiloff on 16.08.2018
 */
@Suppress("unused")
//TODO: подумать чтобы использовать жизненный цикл компонент - LifeCircle
open class FastObserver {
    private val obs = Vector<ProtectedObserverListener>()

    @Synchronized
    fun addObserver(o: ProtectedObserverListener) {
        if (!this.obs.contains(o)) {
            Log.w(this::class.java.simpleName, "addObserver $o")
            this.obs.addElement(o)
        } else {
            Log.w(this::class.java.simpleName, "addObserver doubled $o")
        }
    }

    @Synchronized
    fun deleteObserver(o: ProtectedObserverListener) {
        Log.w(this::class.java.simpleName, "deleteObserver $o")
        this.obs.removeElement(o)
    }

    @Synchronized
    fun deleteObservers() {
        Log.w(this::class.java.simpleName, "deleteObservers")
        obs.removeAllElements()
    }

    fun notifyObservers(arg: Any?) {
        if (obs.isEmpty()) {
            Log.e(this::class.java.simpleName, "no listeners! for $arg")
        } else {
            synchronized(this) { obs.toTypedArray() }
                    .filter {
                        val r = arg !is ProtectedObserver || it.consumerClass() == arg.consumerClass()
                        Log.w("FastObserver",
                                "notifyObservers - "
                                        + (if (r) "processing" else "ignore")
                                        + ":\n event = { $arg }\n to = { $it }")
                        r
                    }
                    .forEach { it.update(this, arg) }
        }
    }
}

interface ProtectedObserverListener {
    fun update(o: FastObserver, arg: Any?)
    fun consumerClass(): String
}
