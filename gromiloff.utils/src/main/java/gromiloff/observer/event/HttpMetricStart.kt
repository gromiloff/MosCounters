package gromiloff.observer.event

import gromiloff.observer.ApplicationObserverImpl

open class HttpMetricStart(val url: String) : ApplicationObserverImpl()