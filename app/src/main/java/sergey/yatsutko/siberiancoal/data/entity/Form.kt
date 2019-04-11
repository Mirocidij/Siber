package sergey.yatsutko.siberiancoal.data.entity

import java.io.Serializable

class Form : Serializable{

    var coalFirm : String = "Аршановский разрез"
    var coalMark : String = "ДМСШ 0-25"
    var pricePerTonn : Int? = 1700
    var address : String = "Введите адресс доставки"
    var weight : Int? = 0
    var distance : Int? = 0
    var deliveryCost : Int? = 0
    var overPrice : Int? = 0

}