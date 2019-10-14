package api.impl

import api.ModelResponse
import api.RequestModel2
import com.google.gson.JsonElement
import obj.CounterAddress
import obj.CounterInfo
import okhttp3.ResponseBody
import retrofit2.Call

class CountersInfoRequest(
    private val modelResponse: ICountersInfoRequest/*, 
                          private val paycode: Long = 1692070089, 
                          private val flat: String = "35"*/) : RequestModel2(modelResponse) {
   
    override fun call(api: API): Call<ResponseBody> = api.getCountersInfo(this.modelResponse.paycode, this.modelResponse.flat)

    override fun parseResponse(element: JsonElement): Any {
        val result = element.asJsonObject
        return Pair<CounterAddress, ArrayList<CounterInfo>>(
            CounterAddress(result.getAsJsonObject("address")),
            result.getAsJsonArray("counter").mapTo(ArrayList()) { CounterInfo(it.asJsonObject) }
        )
    }
}

interface ICountersInfoRequest : ModelResponse {
    val paycode: Long
    val flat: String
}