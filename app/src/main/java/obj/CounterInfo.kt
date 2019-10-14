package obj

import com.google.gson.JsonObject

/**
 * Created by @gromiloff on 26.09.2019
 */
data class CounterInfo(
    val counterId : Long,
    val type : Int,
    val num : String,
    val checkup : String // 2022-08-22+03:00
) {
    constructor(address : JsonObject) : this(
        address.getAsJsonPrimitive("counterId").asLong,
        address.getAsJsonPrimitive("type").asInt,
        address.getAsJsonPrimitive("num").asString,
        address.getAsJsonPrimitive("checkup").asString)
}