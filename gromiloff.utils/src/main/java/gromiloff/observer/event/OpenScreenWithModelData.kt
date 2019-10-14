package gromiloff.observer.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import gromiloff.observer.ActivityObserverImpl
import gromiloff.observer.ViewModelParser

open class OpenScreenWithModelData(private val clz: Class<*>,
                              private val closeCurrent: Boolean = false,
                              private val code: Int = -1,
                              vararg parsers: ViewModelParser<*, *>
) : ActivityObserverImpl() {
    private val parsersList = parsers

    fun fill(activity: Activity) {
        val i = Intent(activity, this.clz)
        if (this.parsersList.isNotEmpty()) {
            val bundle = Bundle()
            this.parsersList.forEach { it.save(bundle) }
            i.putExtras(bundle)
        }
        if (this.closeCurrent) activity.finish()
        activity.startActivityForResult(i, this.code)
    }
}