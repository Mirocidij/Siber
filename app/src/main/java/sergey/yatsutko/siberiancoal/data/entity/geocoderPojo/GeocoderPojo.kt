package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeocoderPojo {

    @SerializedName("response")
    @Expose
    lateinit var response: Response

}
