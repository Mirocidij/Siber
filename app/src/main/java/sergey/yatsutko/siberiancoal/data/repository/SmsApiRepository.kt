package sergey.yatsutko.siberiancoal.data.repository

import android.os.Handler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.network.Sms.SmsService

class SmsApiRepository constructor(phone: String, message: String, handler : Handler) {

    init {
        SmsService
            .instance
            .jsonApi
            .sendSms(phoneNumber = phone, message = message)
            .doOnSubscribe {

            }
            .doOnComplete {
                handler.sendEmptyMessage(1)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }
    

}