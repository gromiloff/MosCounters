package api

import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.*
import base.ApiPref
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gromiloff.observer.ObserverImpl
import gromiloff.observer.event.ShowToast
import gromiloff.observer.event.TryException
import gromiloff.prefs.AppPref
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import pro.gromiloff.mos.counters.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class RequestModel2(private val modelResponse: ModelResponse?) : ObserverImpl, Callback<ResponseBody> {
    init { reset() }
    
    protected object Const {
        const val moduleName = "ajaxModule"
        const val actionName = "ajaxAction"

        const val flatName = "items[flat]"
        const val paycodeName = "items[paycode]"
        
        const val counterName = "items[counterId]"
        const val startPeriodName = "items[startPeriod]"
        const val endPeriodName = "items[endPeriod]"

        const val counterValueName = "items[indications][0][counterVal]"
        const val counterValuePeriodName = "items[indications][0][period]"
        const val counterValueTextName = "items[indications][0][num]"
        const val counterNumberName = "items[indications][0][counterNum]"
    }


    interface API {
        @FormUrlEncoded
        @Headers(
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "user-agent: ${BuildConfig.USER_AGENT}"
        )
        @POST("pgu/common/ajax/index.php")
        fun getCountersInfo(@Field(Const.paycodeName) paycode: Long,
                    @Field(Const.flatName) flat: String,
                    @Field(Const.moduleName) ajaxModule: String = "Guis",
                    @Field(Const.actionName) ajaxAction: String = "getCountersInfo") : Call<ResponseBody>

        @FormUrlEncoded
        @Headers(
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "user-agent: ${BuildConfig.USER_AGENT}"
        )
        @POST("pgu/common/ajax/index.php")
        fun getArchive(@Field(Const.paycodeName) paycode: Long,
                    @Field(Const.flatName) flat: String,
                    @Field(Const.counterName) counterId: Long,
                    @Field(Const.startPeriodName) startPeriod: String,
                    @Field(Const.endPeriodName) endPeriod: String,
                    @Field(Const.moduleName) ajaxModule: String = "Guis",
                    @Field(Const.actionName) ajaxAction: String = "getArchive") : Call<ResponseBody>

        @FormUrlEncoded
        @Headers(
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "user-agent: ${BuildConfig.USER_AGENT}"
        )
        @POST("pgu/common/ajax/index.php")
        fun addCounterInfo(@Field(Const.paycodeName) paycode: Long,
                    @Field(Const.flatName) flat: String,
                    @Field(Const.counterValueName) counterValue: Float,
                    @Field(Const.counterValuePeriodName) counterValuePeriod: String,
                    @Field(Const.counterValueTextName) counterValueText: String,
                    @Field(Const.counterNumberName) counterNumber: Long,
                    @Field(Const.moduleName) ajaxModule: String = "Guis",
                    @Field(Const.actionName) ajaxAction: String = "addCounterInfo") : Call<ResponseBody>
    }

    private var call: Call<ResponseBody>? = null
    private var counts = 0

    abstract fun parseResponse(element: JsonElement): Any
    abstract fun call(api: API): Call<ResponseBody>
    open fun parseError(element: JsonObject): Pair<Boolean, Any?>? = null

    override fun send() {
        try {
            val headers = HashMap<String, String>(1)
            headers["Cookie"] = AppPref.load<String>(ApiPref.Cookie)!!

            @Suppress("BooleanLiteralArgument") val interceptor = UserAgentInterceptor(headers,
                true, 
                false, 
                false)
            this.call = call(
                Retrofit.Builder()
                    .baseUrl("https://www.mos.ru/")
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                    .client(OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .connectTimeout(TimeUnit.SECONDS.toMillis(10), TimeUnit.MILLISECONDS)
                            .build())
                    .build()
                    .create(API::class.java))
            Log.e(this::class.java.simpleName, "try call")
            this.call?.enqueue(this)
        } catch (e: Exception) {
            TryException(e).send()
            Handler(Looper.getMainLooper()).post { this.modelResponse?.error(this, e) }
        }
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        Log.e(this::class.java.simpleName, "onResponse $response")

        if (!call.isCanceled) {
            if (!response.isSuccessful) {
                try {
                    // TODO: если пришла ошибка - стоит прокинуть на пролонгацию куков
                    val error = JsonParser().parse(response.errorBody()!!.string()).asJsonObject
                    val pairError = parseError(error)

                    if (this.modelResponse != null && pairError?.first == true)
                        this.modelResponse.also { Handler(Looper.getMainLooper()).post { this.modelResponse.error(this, data = pairError.second) } }
                    else {
                        tryAgain()
                    }
                    
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post { this.modelResponse?.error(this, e) }
                }
            } else {
                reset()

                try {
                    val a = JsonParser().parse(response.body()!!.charStream())
                    val answer = parseResponse(if (a is JsonObject && a.has("result")) a.asJsonObject.get("result") else a)
                    this.modelResponse?.success(this, answer)
                } catch (e: Exception) {
                    TryException(e).send()
                    Handler(Looper.getMainLooper()).post { this.modelResponse?.error(this, e) }
                }
            }
        } else {
            reset()
        }
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.e(this::class.java.simpleName, "onFailure $t")
        TryException(t).send()
        if (!call.isCanceled) {
            Handler(Looper.getMainLooper()).post { this.modelResponse?.error(this, t) }
            tryAgain()
        }
    }

    fun killMePls() {
        this.call?.also {
            Log.e(this::class.java.simpleName, "killMePls")
            it.cancel()
            this.call = null
        }
    }

    private fun reset() {
        this.counts = 3
    }

    private fun tryAgain() {
        if (--this.counts >= 0) {
            try {
                synchronized(this) { Thread.sleep(10 * DateUtils.SECOND_IN_MILLIS) }
                if (this.call == null) return
                send()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        } else {
            // ошибка
            Handler(Looper.getMainLooper()).post { this.modelResponse?.error(this) }
        }
        Log.e(this::class.java.simpleName, "tryAgain $counts")
    }
}

interface ModelResponse {
    @AnyThread
    fun success(it: RequestModel2?, response: Any?)

    @MainThread
    fun error(it: RequestModel2?, t: Throwable? = null, data: Any? = null)
}

abstract class EmptyModelResponse : ViewModel(), LifecycleObserver, ModelResponse {
    private val cache = HashMap<String, RequestModel2>()

    override fun error(it: RequestModel2?, t: Throwable?, data: Any?) {
        ShowToast(
                when (t) {
                    is SocketTimeoutException -> "Истекло время ожидания ответа сервера. Проверьте подключение к интернету."
                    is UnknownHostException -> "Нет подключения к интернету. Проверьте подключение и перезапустите приложение."
                    null -> "Неизвестная ошибка"
                    else -> "Что-то пошло не так :("
                })
                .send()
    }

    @CallSuper
    open fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.i(this::class.java.simpleName, "onStateChanged >> $source >> $event")
        if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
            this.cache.values.forEach { it.killMePls() }
        }
    }

    protected fun addRequestInstance(instance: RequestModel2): String {
        val key = instance::class.java.canonicalName!! + "_${System.currentTimeMillis()}"
        this.cache[key] = instance
        return key
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : RequestModel2> getRequestInstance(key: String?) = this.cache[key] as? T
}

abstract class SingleModelResponse<REQUEST : RequestModel2> : EmptyModelResponse() {
    private var requestKey: String? = null

    abstract fun createRequest(): REQUEST

    protected fun get() = getRequestInstance<REQUEST>(this.requestKey)

    @CallSuper
    open fun reload() {
        if(this.requestKey == null) this.requestKey = addRequestInstance(createRequest())
        get()?.send()
    }
    
    
}

abstract class SingleLiveModelResponse<REQUEST : RequestModel2, Answer> : SingleModelResponse<REQUEST>() {
    //TODO: возможно стоит освобождать при изменении состояния ?
    val liveData: MutableLiveData<Answer?> = MutableLiveData()

    @Suppress("UNCHECKED_CAST")
    @AnyThread
    override fun success(it: RequestModel2?, response: Any?) {
        this.liveData.postValue(response as Answer)
    }
}