package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response {

    @SerializedName("GeoObjectCollection")
    @Expose
    lateinit var geoObjectCollection: GeoObjectCollection

}
