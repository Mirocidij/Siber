package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdministrativeArea {

    @SerializedName("AdministrativeAreaName")
    @Expose
    lateinit var administrativeAreaName: String
    @SerializedName("SubAdministrativeArea")
    @Expose
    lateinit var subAdministrativeArea: SubAdministrativeArea

}
