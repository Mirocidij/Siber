package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Premise {

    @SerializedName("PremiseNumber")
    @Expose
    lateinit var premiseNumber: String
    @SerializedName("PostalCode")
    @Expose
    lateinit var postalCode: PostalCode
}
