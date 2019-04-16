package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeocoderResponseMetaData {

    @SerializedName("request")
    @Expose
    lateinit var request: String
    @SerializedName("found")
    @Expose
    lateinit var found: String
    @SerializedName("results")
    @Expose
    lateinit var results: String

}
