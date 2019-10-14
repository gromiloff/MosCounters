package gromiloff.observer.event

import gromiloff.observer.ApplicationObserverImpl

open class HttpMetricStop(val url: String, val code: Int = -1, val status: String) : ApplicationObserverImpl()