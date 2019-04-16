package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Country {

    @SerializedName("AddressLine")
    @Expose
    lateinit var addressLine: String
    @SerializedName("CountryNameCode")
    @Expose
    lateinit var countryNameCode: String
    @SerializedName("CountryName")
    @Expose
    lateinit var countryName: String
    @SerializedName("AdministrativeArea")
    @Expose
    lateinit var administrativeArea: AdministrativeArea

}
