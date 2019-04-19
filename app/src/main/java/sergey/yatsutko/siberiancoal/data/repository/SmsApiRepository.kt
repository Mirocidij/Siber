package sergey.yatsutko.siberiancoal.data.repository

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.network.sms.SmsRequest
import sergey.yatsutko.siberiancoal.data.network.sms.SmsApiHolder

class SmsApiRepository(private val api: SmsRequest = SmsApiHolder.api) {

    fun sendSms(phoneNumber: String, message: String): Completable =
        api.sendSms(phoneNumber = phoneNumber, message = message)
            .subscribeOn(Schedulers.io())

}