package gromiloff.observer.event

import gromiloff.observer.ActivityObserverImpl

open class OperationFailed(val msg : String? = null) : ActivityObserverImpl()