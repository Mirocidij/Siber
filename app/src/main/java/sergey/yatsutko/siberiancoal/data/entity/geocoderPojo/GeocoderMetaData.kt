package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeocoderMetaData {

    @SerializedName("kind")
    @Expose
    lateinit var kind: String
    @SerializedName("text")
    @Expose
    lateinit var text: String
    @SerializedName("precision")
    @Expose
    lateinit var precision: String
    @SerializedName("Address")
    @Expose
    lateinit var address: Address
    @SerializedName("AddressDetails")
    @Expose
    lateinit var addressDetails: AddressDetails

}
