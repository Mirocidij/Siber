package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DependentLocality_ {

    @SerializedName("DependentLocalityName")
    @Expose
    lateinit var dependentLocalityName: String
    @SerializedName("Thoroughfare")
    @Expose
    lateinit var thoroughfare: Thoroughfare_

}
