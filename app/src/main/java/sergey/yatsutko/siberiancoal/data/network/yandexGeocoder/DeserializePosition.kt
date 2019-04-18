package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import com.google.gson.JsonDeserializer
import sergey.yatsutko.siberiancoal.data.entity.Position

object DeserializePosition {
    val route = JsonDeserializer { json, _, _ ->
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

        Position(isHouse = isHouse, position = position)
    }

}