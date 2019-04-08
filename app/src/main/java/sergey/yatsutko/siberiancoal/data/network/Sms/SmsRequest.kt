package sergey.yatsutko.siberiancoal.data.network.Sms

import io.reactivex.Completable
import retrofit2.http.POST
import retrofit2.http.Query

// https://smsc.ru/sys/send.php?login=lflagmanl&psw=eujkmkexitdct[!&phone=9527464115&mes=Hello

interface SmsRequest {
    @POST("/sys/send.php?login=lflagmanl&psw=eujkmkexitdct[!&")
    fun sendSms(
        @Query("phones") phoneNumber: String,
        @Query("mes") message: String
    ): Completable
}