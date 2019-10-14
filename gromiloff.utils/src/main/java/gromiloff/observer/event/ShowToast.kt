package gromiloff.observer.event

import android.content.res.Resources
import gromiloff.observer.ActivityObserverImpl

open class ShowToast(private val message: Any) : ActivityObserverImpl() {
    fun string(resources: Resources): String = if (message is Int) resources.getString(message) else message as String
}