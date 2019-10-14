package gromiloff.observer.event

import android.content.pm.PackageManager
import gromiloff.observer.ActivityAndFragmentObserverImpl

class PermissionResponse(val permission: String, val status: Int = PackageManager.PERMISSION_GRANTED)
    : ActivityAndFragmentObserverImpl()