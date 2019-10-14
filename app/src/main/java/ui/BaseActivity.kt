package ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import gromiloff.observer.ObserverImpl
import gromiloff.observer.event.LoadingStart
import gromiloff.observer.event.LoadingStop
import gromiloff.observer.impl.ObserverActivity
import pro.gromiloff.mos.counters.R

abstract class BaseActivity<T : ViewModel> : ObserverActivity<T>() {
    companion object {
        fun <T : ViewModel> getActivityModel(fragment: Fragment): T? {
            val a = fragment.activity
            @Suppress("UNCHECKED_CAST")
            return if (a is BaseActivity<*>) a.getModel() as T else null
        }
    }

    private var loading: View? = null
    protected var toolbar: Toolbar? = null
    var finishByStop = false

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.finishByStop = false
        super.onCreate(savedInstanceState)
        this.loading = findViewById(R.id.app_loading)        
        this.toolbar = findViewById(R.id.app_toolbar)
        if (this.toolbar != null) setSupportActionBar(this.toolbar)
        
        //this.toolbar?.setOnClickListener { ToolbarClicked().send() }
        /*this.toolbar?.setOnLongClickListener {
            ShowToast("App version v${BuildConfig.VERSION_NAME}").send()
            false
        }*/
    }
    
    override fun onStop() {
        super.onStop()
        if (this.finishByStop) finish()
    }

    override fun command(arg: ObserverImpl) {
        when (arg) {
            is LoadingStart -> runOnUiThread { this.loading?.visibility = View.VISIBLE }
            is LoadingStop -> runOnUiThread { this.loading?.visibility = if(arg.asGone) View.GONE else View.INVISIBLE }
            else -> super.command(arg)
        }
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //when(requestCode){}
    }*/

}
