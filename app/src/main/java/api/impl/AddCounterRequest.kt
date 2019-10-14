package api.impl

import api.RequestModel2
import com.google.gson.JsonElement
import obj.CounterInfo
import okhttp3.ResponseBody
import retrofit2.Call

/**
ajaxModule: Guis
ajaxAction: addCounterInfo
items[paycode]: 1692070089
items[flat]: 35
items[indications][0][counterNum]: 579442
items[indications][0][counterVal]: 450
items[indications][0][num]: №029665
items[indications][0][period]: 2019-04-30
 * */
class AddCounterRequest(private val modelResponse: IAddCounterRequest) : RequestModel2(modelResponse) {
    override fun call(api: API): Call<ResponseBody> = api.addCounterInfo(
        this.modelResponse.paycode,
        this.modelResponse.flat,
        this.modelResponse.counterValue,
        this.modelResponse.counter.num,
        this.modelResponse.counterValuePeriod,
        this.modelResponse.counter.counterId)

    override fun parseResponse(element: JsonElement): Any {
        val result = element.asJsonObject
        return true
    }
}

interface IAddCounterRequest : ICountersInfoRequest {
    var counter: CounterInfo
    var counterValuePeriod: String
    var counterValue: Float
}