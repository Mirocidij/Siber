package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeoObjectCollection {

    @SerializedName("metaDataProperty")
    @Expose
    lateinit var metaDataProperty: MetaDataProperty
    @SerializedName("featureMember")
    @Expose
    lateinit var featureMember: List<FeatureMember>

}
