package gromiloff.observer.impl

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import gromiloff.observer.*
import gromiloff.observer.event.OpenScreenWithModelData
import gromiloff.observer.event.PermissionRequest
import gromiloff.observer.event.PermissionResponse
import gromiloff.observer.event.ShowToast
import java.util.*

abstract class ObserverActivity<T : ViewModel> : AppCompatActivity(), ProtectedObserverListener {
    object Const {
        const val permissions = 999
    }
    private val stackEventsByResume = Stack<ObserverImpl>()
    
    @LayoutRes
    protected open fun layoutId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityObserver.addObserver(this)
        super.onCreate(savedInstanceState)
        val l = layoutId()
        if (l > 0) {
            setContentView(l)
        }

        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.addObserver(model)
        (getModel() as? ParserBaseImpl<*>)?.load(savedInstanceState, intent?.extras)
    }

    override fun onStart() {
        ActivityObserver.addObserver(this)
        super.onStart()
    }

    override fun onResume() {
        ActivityObserver.addObserver(this)
        this.stackEventsByResume.forEach { it.send() }
        this.stackEventsByResume.clear()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        (getModel() as? ParserFullImpl<*>)?.save(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        (getModel() as? ParserFullImpl<*>)?.load(savedInstanceState)
        super.onSaveInstanceState(savedInstanceState)
    }
    
    override fun onPause() {
        super.onPause()
        ActivityObserver.deleteObserver(this)
    }

    override fun onDestroy() {
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.removeObserver(model)
        super.onDestroy()
    }
    
    override fun update(o: FastObserver, arg: Any?) {
        when (arg) {
            is OpenScreenWithModelData -> arg.fill(this)
            else -> command(arg as ObserverImpl)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Const.permissions -> {
                permissions.forEachIndexed { index, s ->
                    this.stackEventsByResume.push(PermissionResponse(s, grantResults[index]))
                }
            }
        }
    }

    override fun consumerClass() = toString()

    fun getModel() = ViewModelProviders.of(this).get(EmptyBaseModel.ME, getModelClass())

    @Suppress("UNCHECKED_CAST")
    open fun getModelClass(): Class<T> = EmptyBaseModel::class.java as Class<T>

    @CallSuper
    open fun command(arg: ObserverImpl) {
        when(arg){
            is PermissionRequest -> try {
                ActivityCompat.requestPermissions(this, arrayOf(arg.permissions), Const.permissions)
            } catch (e: Exception) {
                //  Crashlytics.logException(e)
                arg.permissions.forEach { PermissionResponse(it.toString(), PackageManager.PERMISSION_DENIED).send() }
            }
            is ShowToast ->
                runOnUiThread {
                    val t = Toast.makeText(this, arg.string(resources), Toast.LENGTH_LONG)
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                }
            
        }
    }
}
