package base

import gromiloff.prefs.PrefEnum

enum class ApiPref(override val defaultValue: Any) : PrefEnum<Any> {
    Cookie(""),
    
    ;
}
