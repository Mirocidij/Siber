package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Component {

    @SerializedName("kind")
    @Expose
    lateinit var kind: String
    @SerializedName("name")
    @Expose
    lateinit var name: String

}
