package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MetaDataProperty {

    @SerializedName("GeocoderResponseMetaData")
    @Expose
    lateinit var geocoderResponseMetaData: GeocoderResponseMetaData

}
