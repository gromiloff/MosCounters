package api.impl

import api.RequestModel2
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Call

/**
ajaxModule: Guis
ajaxAction: getArchive
items[paycode]: 1692070089
items[flat]: 35
items[counterId]: 579440
items[startPeriod]: 2019-01-01
items[endPeriod]: 2019-09-01
 * */
class ArchiveRequest(private val modelResponse: IArchiveRequest) : RequestModel2(modelResponse) {
    override fun call(api: API): Call<ResponseBody> = api.getArchive(
        this.modelResponse.paycode,
        this.modelResponse.flat,
        this.modelResponse.counterId,
        this.modelResponse.startPeriod,
        this.modelResponse.endPeriod)

    override fun parseResponse(element: JsonElement): Any {
        val result = element.asJsonObject
        return true
    }
}

interface IArchiveRequest : ICountersInfoRequest {
    var counterId: Long
    var startPeriod: String
    var endPeriod: String
}