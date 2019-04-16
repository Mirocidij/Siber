package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeoObject {

    @SerializedName("metaDataProperty")
    @Expose
    lateinit var metaDataProperty: MetaDataProperty_
    @SerializedName("description")
    @Expose
    lateinit var description: String
    @SerializedName("name")
    @Expose
    lateinit var name: String
    @SerializedName("boundedBy")
    @Expose
    lateinit var boundedBy: BoundedBy
    @SerializedName("Point")
    @Expose
    lateinit var point: Point

}
