package base

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import androidx.multidex.MultiDex
import event.RequestLogin
import gromiloff.observer.FastObserver
import gromiloff.observer.event.HttpMetricStart
import gromiloff.observer.event.HttpMetricStop
import gromiloff.observer.event.ShowToast
import gromiloff.observer.event.TryException
import gromiloff.observer.impl.ObserverApplication
import gromiloff.prefs.AppPref
import java.util.*

class Main : ObserverApplication() {
    //private val metrics = HashMap<String, HttpMetric>()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        //  val timeoutSession = 10 * 60

        AppPref.init(this.applicationContext, "zoon-b2b")
        super.onCreate()

 //       FirebaseApp.initializeApp(this)
        //     FirebaseAnalytics.getInstance(this).setSessionTimeoutDuration(timeoutSession * DateUtils.SECOND_IN_MILLIS)
//        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = !BuildConfig.DEBUG

     /*   Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics.Builder().core(CrashlyticsCore.Builder().build()).build())
                .debuggable(!BuildConfig.DEBUG)
                .build()
        )*/

        // Change locale settings in the app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val conf = resources.configuration
            conf.setLocale(Locale("ru"))
            createConfigurationContext(conf)
        }
    }

    @CallSuper
    override fun update(o: FastObserver, arg: Any?) {
        when (arg) {
            is RequestLogin -> {
                //base.ApplicationUtils.restartApplication(this, Pin::class.java)
            }
            is TryException -> {
                arg.e.printStackTrace()
              //  Crashlytics.getInstance().core.logException(arg.e)
            }
            is HttpMetricStart -> {
            //    var metric = metrics[arg.url]
             /*   if (metric == null) {
                 //   metric = FirebasePerformance.getInstance().newHttpMetric(arg.url, FirebasePerformance.HttpMethod.POST)
                }
                metric.start()*/
            }
            is HttpMetricStop -> {
                /*val metric = metrics.remove(arg.url)
                metric?.putAttribute("Status", arg.status)
                metric?.setHttpResponseCode(arg.code)
                metric?.stop()*/
            }
        }
    }

    object Instance {
        fun from(context: Context) = context.applicationContext as Main
        fun from(view: View) = from(view.context)
        fun activityFrom(view: View): FragmentActivity? {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is FragmentActivity) {
                    return context
                }
                context = context.baseContext
            }
            return null
        }

        // @Suppress("UNCHECKED_CAST")
        //fun <T : ViewModel> baseFeatureActivityFrom(view: View) = activityFrom(view) as BaseFeatureActivity<T>
    }
}

class ApplicationUtils {
    companion object {
        fun restartApplication(c: Context, cls: Class<*>) {
            val intent = Intent(c, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            c.startActivity(intent)
        }
    }

    object Open {
        fun dial(context: Context, url: String) {
            try {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                ShowToast("Отсутствует приложение для осуществления звонка").send()
            } catch (e: Exception) {
                ShowToast("Ошибка").send()
            }
        }

        fun mailto(context: Context, url: String) {
            try {
                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                ShowToast("Отсутствует приложение для отправки почты").send()
            } catch (e: Exception) {
                ShowToast("Ошибка").send()
            }
        }
        

        fun view(context: Context, url: String) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                ShowToast("Не найдено приложений для осуществления действия").send()
            } catch (e: Exception) {
                ShowToast("Ошибка").send()
            }
        }
    }

    object Notification {
        fun putIt(context: Context,
                  title: String? = null,
                  message: String,
                  id: Int = 1000 + Random().nextInt(1000),
                  channel: String = "zoon.channel"
        ) {
            if (true) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var notificationChannel = manager.getNotificationChannel(channel)

                    if (notificationChannel == null) {
                        notificationChannel = NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_DEFAULT).apply { description = channel }
                        manager.createNotificationChannel(notificationChannel)
                    }
                }

                val a = NotificationCompat.Builder(context, channel)
                        //.setSmallIcon(smallIcon)
                        .setContentTitle(title)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                manager.notify(id, a.build())
            }
        }
    }
}
