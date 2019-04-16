package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Locality {

    @SerializedName("LocalityName")
    @Expose
    lateinit var localityName: String
    @SerializedName("Thoroughfare")
    @Expose
    lateinit var thoroughfare: Thoroughfare
    @SerializedName("DependentLocality")
    @Expose
    lateinit var dependentLocality: DependentLocality

}
