package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Address {

    @SerializedName("country_code")
    @Expose
    lateinit var countryCode: String
    @SerializedName("postal_code")
    @Expose
    lateinit var postalCode: String
    @SerializedName("formatted")
    @Expose
    lateinit var formatted: String
    @SerializedName("Components")
    @Expose
    lateinit var components: List<Component>

}
