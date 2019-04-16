package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeatureMember {

    @SerializedName("GeoObject")
    @Expose
    lateinit var geoObject: GeoObject

}
