package gromiloff.observer.event

import gromiloff.observer.ActivityObserverImpl

class PermissionRequest(val permissions: String) : ActivityObserverImpl()