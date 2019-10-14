package obj

import com.google.gson.JsonObject

/**
 * Created by @gromiloff on 26.09.2019
 */
data class CounterAddress(
    val street : String,
    val house : String,
    val flat : String,
    val okrug : String,
    val district : String,
    val unom : String
) {
    constructor(address : JsonObject) : this(
        address.getAsJsonPrimitive("street").asString, 
        address.getAsJsonPrimitive("house").asString, 
        address.getAsJsonPrimitive("flat").asString,
        address.getAsJsonPrimitive("okrug").asString,
        address.getAsJsonPrimitive("district").asString,
        address.getAsJsonPrimitive("unom").asString)
}