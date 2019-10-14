package gromiloff.observer.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import gromiloff.observer.FastObserver
import gromiloff.observer.FragmentObserver
import gromiloff.observer.ProtectedObserverListener

abstract class ObserverFragment<T : ViewModel> : Fragment(), ProtectedObserverListener {
    abstract fun getModelClass(): Class<T>

    fun getModel() = ViewModelProviders.of(this).get(getModelClass())

    @LayoutRes
    protected abstract fun layoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.addObserver(model)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(layoutId(), container, false)!!

    override fun onStart() {
        super.onStart()
        FragmentObserver.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        FragmentObserver.addObserver(this)
    }

    override fun onStop() {
        super.onStop()
        FragmentObserver.deleteObserver(this)
    }

    override fun onDestroy() {
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.removeObserver(model)
        FragmentObserver.deleteObserver(this)
        super.onDestroy()
    }

    @CallSuper
    override fun update(o: FastObserver, arg: Any?) {
    }

    override fun consumerClass() = toString()

    @AnyThread
    fun showDialog(dialog: DialogFragment) {
        activity?.runOnUiThread {
            if (fragmentManager != null) dialog.show(fragmentManager!!, dialog.javaClass.simpleName)
        }
    }
}
