package base

import android.os.Bundle

/**
 * Created by @gromiloff on 04.12.2018
 */
interface ViewModelParser<ME : ViewModelParser<ME, DATA>, DATA> {
    fun save(data: DATA?)
    fun save(bundle: Bundle)
    fun load(bundle: Bundle?): ME
}