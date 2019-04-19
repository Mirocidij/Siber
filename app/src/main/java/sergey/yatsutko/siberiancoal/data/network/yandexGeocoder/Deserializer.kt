package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import com.google.gson.JsonDeserializer
import sergey.yatsutko.siberiancoal.data.entity.Address
import sergey.yatsutko.siberiancoal.data.entity.Position

object Deserializer {
    val address = JsonDeserializer { json, _, _ ->
        val obj = json.asJsonObject
            .getAsJsonObject("response")
            .getAsJsonObject("GeoObjectCollection")
            .getAsJsonArray("featureMember")[0].asJsonObject
            .getAsJsonObject("GeoObject")

        val kind = obj
            .getAsJsonObject("metaDataProperty")
            .getAsJsonObject("GeocoderMetaData")
            .getAsJsonPrimitive("kind").asString

        val isHouse = kind == "house"

        var address = if (isHouse) {
            obj.getAsJsonObject("metaDataProperty")
                .getAsJsonObject("GeocoderMetaData")
                .getAsJsonPrimitive("text").asString
        } else {
            null
        }

        address?.split(", ")?.let {
            if (it.size >= 3) {
                address = it.reversed().joinToString(", ")
            }
        }

        Address(address = address)
    }

    val position = JsonDeserializer { json, _, _ ->
        val obj = json.asJsonObject
            .getAsJsonObject("response")
            .getAsJsonObject("GeoObjectCollection")
            .getAsJsonArray("featureMember")[0].asJsonObject
            .getAsJsonObject("GeoObject")

        val kind = obj
            .getAsJsonObject("metaDataProperty")
            .getAsJsonObject("GeocoderMetaData")
            .getAsJsonPrimitive( "kind").asString

        val isHouse = kind == "house"

        val position = if(isHouse) {
            obj.getAsJsonObject("Point")
                .getAsJsonPrimitive("pos").asString
                .split(" ")
                .map { it.toDouble() }
        } else {
            null
        }

        Position(position = position)
    }
}