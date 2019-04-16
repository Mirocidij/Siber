package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Thoroughfare {

    @SerializedName("ThoroughfareName")
    @Expose
    lateinit var thoroughfareName: String
    @SerializedName("Premise")
    @Expose
    lateinit var premise: Premise

}
