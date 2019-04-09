package sergey.yatsutko.siberiancoal.data.network.sms

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object SmsService {

    private const val BASE_URL = "https://smsc.ru"

    val api: SmsRequest = initApi()

    private fun initApi(): SmsRequest {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(SmsRequest::class.java)
    }
}
