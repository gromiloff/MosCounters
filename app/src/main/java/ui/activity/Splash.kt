package ui.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.annotation.LayoutRes
import base.ApiPref
import gromiloff.observer.EmptyBaseModel
import gromiloff.observer.event.OpenScreenWithModelData
import gromiloff.prefs.AppPref
import pro.gromiloff.mos.counters.R
import ui.BaseActivity

class Splash : BaseActivity<EmptyBaseModel>() {
    private var webPage: WebView? = null

    @LayoutRes
    override fun layoutId() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
        
    }

    override fun onResume() {
        super.onResume()

        OpenScreenWithModelData(CreateCookie::class.java, true).send()
/*
        OpenScreenWithModelData(if(AppPref.load(ApiPref.Cookie, "")?.length ?: 0 > 0){
            // куки в наличии, процессимся на следующий экран
            Main::class.java
        }
        else {
            // требуются куки
            CreateCookie::class.java
        }, true).send()*/
    }
}
