package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import com.google.gson.JsonDeserializer
import sergey.yatsutko.siberiancoal.data.entity.Address

object DeserializeAddress {
    val route = JsonDeserializer { json, _, _ ->
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

        val streetName = address!!.split(", ")

        if (streetName.size >= 3) {
            address =
                streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
        }

        Address(isHouse = isHouse, address = address)
    }
}