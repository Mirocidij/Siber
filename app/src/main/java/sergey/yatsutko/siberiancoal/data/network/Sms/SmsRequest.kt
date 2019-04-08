package sergey.yatsutko.siberiancoal.data.network.Sms

import io.reactivex.Completable
import retrofit2.http.POST
import retrofit2.http.Query

interface SmsRequest {
    @POST("/sys/send.php?login=lflagmanl&psw=eujkmkexitdct[!&")
    fun sendSms(
        @Query("phone") phoneNumber: String,
        @Query("mes") message: String
    ): Completable
}