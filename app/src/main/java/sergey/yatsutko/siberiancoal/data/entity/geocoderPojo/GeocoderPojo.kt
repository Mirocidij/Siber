package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeocoderPojo {

    @SerializedName("response")
    @Expose
    lateinit var response: Response

    val kind = response.geoObjectCollection
        .featureMember[0]
        .geoObject
        .metaDataProperty
        .geocoderMetaData
        .kind


    val point = response.geoObjectCollection
        .featureMember[0]
        .geoObject
        .point
        .pos

    val address = response.geoObjectCollection
        .featureMember[0]
        .geoObject
        .metaDataProperty
        .geocoderMetaData
        .text
}
