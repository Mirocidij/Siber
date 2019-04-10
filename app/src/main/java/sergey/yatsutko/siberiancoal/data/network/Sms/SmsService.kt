package sergey.yatsutko.siberiancoal.data.network.Sms

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SmsService private constructor() {
    private val mRetrofit: Retrofit

    val jsonApi: SmsRequest
        get() = mRetrofit.create(SmsRequest::class.java)

    init {
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun sendSms(message: String, phone: String) {
        SmsService.instance
            .jsonApi
            .sendSms(
                phone,
                message
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    companion object {

        private var mInstance: SmsService? = null
        private const val BASE_URL = "https://smsc.ru"

        val instance: SmsService
            get() : SmsService {
                if (mInstance == null) {
                    mInstance = SmsService()
                }
                return mInstance!!
            }
    }
}
