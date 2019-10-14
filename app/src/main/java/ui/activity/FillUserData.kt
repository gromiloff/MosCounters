package ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.LayoutRes
import base.ApiPref
import gromiloff.observer.EmptyBaseModel
import gromiloff.prefs.AppPref
import org.jsoup.Connection
import org.jsoup.Jsoup
import pro.gromiloff.mos.counters.BuildConfig
import pro.gromiloff.mos.counters.R
import ui.BaseActivity


class FillUserData : BaseActivity<EmptyBaseModel>() {
    private var webPage: WebView? = null
    private var loginSuccess = false

    @LayoutRes
    override fun layoutId() = R.layout.activity_fill_user_data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        CookieManager.getInstance().setAcceptCookie(true)
        this.webPage = findViewById(R.id.web)
        
        @SuppressLint("SetJavaScriptEnabled")
        this.webPage?.settings?.javaScriptEnabled = true
        this.webPage?.settings?.domStorageEnabled = true

        this.webPage?.settings?.userAgentString = BuildConfig.USER_AGENT
        
        this.webPage?.webChromeClient = object : WebChromeClient(){}
        this.webPage?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e("TAG", "onPageFinished\n$url")
                when (url) {
                    "https://www.mos.ru/", "https://mos.ru/" -> {
                        if(loginSuccess){
                            AppPref.save(ApiPref.Cookie, CookieManager.getInstance().getCookie(url))
                            finish()
                        } else {
                            loginSuccess = false
                            view.loadUrl("https://www.mos.ru/api/acs/v1/login?back_url=https%3A%2F%2Fwww.mos.ru%2F")
                        }
                    }
                    "https://login.mos.ru/sps/login/methods/password" -> view.visibility = View.VISIBLE
                }                
                //super.onPageFinished(view, url)
            }
            
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.e("TAG", "onPageStarted\n$url")
                val check = true == url?.contains("login/satisfy?code=")
                if(url == "https://mos.ru/" || url == "https://wwww.mos.ru/" || check){
                    view?.visibility = View.INVISIBLE
                    if(check) loginSuccess = true
                }
                super.onPageStarted(view, url, favicon)
            }

        }
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) CookieManager.getInstance().removeAllCookies {  } else CookieManager.getInstance().removeAllCookie()
        this.webPage?.loadUrl("https://mos.ru/")
    }
}
