package sergey.yatsutko.siberiancoal.data.network.Sms

import io.reactivex.Completable
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface SmsRequest {
    @POST("/sys/send.php")
    fun sendSms(
        @Query("login") login: String,
        @Query("psw") password: String,
        @Query("phones") phoneNumber: String,
        @Query("mes") message: String
    ): Call<Completable>
}