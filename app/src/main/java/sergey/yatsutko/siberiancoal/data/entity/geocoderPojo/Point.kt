package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Point {

    @SerializedName("pos")
    @Expose
    lateinit var pos: String

}
