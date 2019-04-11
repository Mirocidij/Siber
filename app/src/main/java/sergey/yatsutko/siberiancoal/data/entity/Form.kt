package sergey.yatsutko.siberiancoal.data.entity

import java.io.Serializable

class Form : Serializable{

    lateinit var coalFirm : String
    lateinit var coalMark : String
    var pricePerTonn : Int? = null
    var weight : Int? = null
    lateinit var address : String
    var distance : Int? = null
    var deliveryPrice : Int? = null
    var overPrice : Int? = null

}