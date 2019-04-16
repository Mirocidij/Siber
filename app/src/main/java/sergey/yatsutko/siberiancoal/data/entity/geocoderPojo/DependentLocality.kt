package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DependentLocality {

    @SerializedName("DependentLocalityName")
    @Expose
    lateinit var dependentLocalityName: String
    @SerializedName("DependentLocality")
    @Expose
    lateinit var dependentLocality: DependentLocality_
    @SerializedName("Thoroughfare")
    @Expose
    lateinit var thoroughfare: Thoroughfare__

}
