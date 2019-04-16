package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostalCode {

    @SerializedName("PostalCodeNumber")
    @Expose
    lateinit var postalCodeNumber: String

}
