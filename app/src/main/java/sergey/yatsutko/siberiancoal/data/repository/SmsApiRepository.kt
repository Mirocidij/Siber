package sergey.yatsutko.siberiancoal.data.repository

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.network.sms.SmsRequest
import sergey.yatsutko.siberiancoal.data.network.sms.SmsService

class SmsApiRepository constructor(private val api: SmsRequest = SmsService.api) {

    fun sendSms(phoneNumber: String, message: String): Completable =
        api.sendSms(phoneNumber = phoneNumber, message = message)
            .subscribeOn(Schedulers.io())

}