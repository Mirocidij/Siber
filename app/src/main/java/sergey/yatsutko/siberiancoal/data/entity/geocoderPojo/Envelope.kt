package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Envelope {

    @SerializedName("lowerCorner")
    @Expose
    lateinit var lowerCorner: String
    @SerializedName("upperCorner")
    @Expose
    lateinit var upperCorner: String

}
