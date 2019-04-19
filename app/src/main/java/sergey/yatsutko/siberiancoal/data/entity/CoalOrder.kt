package sergey.yatsutko.siberiancoal.data.entity

import sergey.yatsutko.siberiancoal.App
import java.io.Serializable

class CoalOrder(
    var coalFirm : String = "Аршановский разрез",
    var coalMark : String = "ДМСШ 0-25",
    var pricePerTonn : Int = 1700,
    var address : String = "",
    var weight : Int = 0,
    var distance : Int = 0,
    var distanceCost : Int = 0,
    var deliveryCost : Int = 0,
    var overPrice : Int = 0,
    var routeStartLocation: List<Double> = listOf(App.cuts[0][0], App.cuts[1][0]),
    var routeEndLocation: List<Double> = listOf(0.0, 0.0),
    var phoneNumber: String = ""
): Serializable