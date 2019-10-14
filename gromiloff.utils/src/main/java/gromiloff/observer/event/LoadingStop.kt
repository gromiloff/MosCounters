package gromiloff.observer.event

import gromiloff.observer.ActivityObserverImpl

open class LoadingStop(val asGone : Boolean = false) : ActivityObserverImpl()