package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SubAdministrativeArea {

    @SerializedName("SubAdministrativeAreaName")
    @Expose
    lateinit var subAdministrativeAreaName: String
    @SerializedName("Locality")
    @Expose
    lateinit var locality: Locality

}
