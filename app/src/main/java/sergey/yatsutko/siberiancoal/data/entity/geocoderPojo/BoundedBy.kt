package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BoundedBy {

    @SerializedName("Envelope")
    @Expose
    lateinit var envelope: Envelope

}
