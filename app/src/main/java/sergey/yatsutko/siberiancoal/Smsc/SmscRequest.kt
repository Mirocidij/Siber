package sergey.yatsutko.siberiancoal.Smsc

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SmscRequest {
    @GET("/sys/send.php")
    fun sendSms(
        @Query("login") login: String,
        @Query("psw") password: String,
        @Query("phones") phoneNumber: String,
        @Query("mes") message: String
    ): Call<Array<String>>
}

//?charset=utf-8&translit=0&