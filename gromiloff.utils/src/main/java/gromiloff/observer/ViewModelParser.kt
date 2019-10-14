package gromiloff.observer

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel


interface ViewModelParser<ME : ViewModelParser<ME, DATA>, DATA> : ParserFullImpl<ME> {
    fun save(data: DATA?)
}

interface ParserBaseImpl<ME : ParserBaseImpl<ME>> {
    fun load(vararg bundle: Bundle?): ME
}

interface ParserFullImpl<ME : ParserFullImpl<ME>> : ParserBaseImpl<ME> {
    fun save(out: Bundle)
}

open class EmptyBaseModel : ViewModel(), ParserFullImpl<EmptyBaseModel> {

    @CallSuper
    override fun save(out: Bundle) {}

    @CallSuper
    override fun load(vararg bundle: Bundle?) = this

    companion object {
        const val ME = "EmptyBaseModel.ME"
    }
}
