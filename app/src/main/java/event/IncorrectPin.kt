package event

import gromiloff.observer.ActivityObserverImpl

class IncorrectPin(val noInternet: Boolean = false) : ActivityObserverImpl()