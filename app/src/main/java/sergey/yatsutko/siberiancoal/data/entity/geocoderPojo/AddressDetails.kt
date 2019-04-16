package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressDetails {

    @SerializedName("Country")
    @Expose
    lateinit var country: Country

}
