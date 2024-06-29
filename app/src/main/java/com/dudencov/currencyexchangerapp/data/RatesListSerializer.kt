package com.dudencov.currencyexchangerapp.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

object RateListSerializer :
    JsonTransformingSerializer<List<CurrencyRate>>(ListSerializer(CurrencyRate.serializer())) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val res = element
            .jsonObject
            .entries
            .toList()
            .map {
                JsonObject(
                    mapOf(
                        Pair(currencySerialName, JsonPrimitive(it.key)),
                        Pair(rateSerialName, it.value)
                    )
                )
            }
        return JsonArray(res)
    }
}