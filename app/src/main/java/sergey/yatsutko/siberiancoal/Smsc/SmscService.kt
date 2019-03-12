package sergey.yatsutko.siberiancoal.Smsc

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SmscService private constructor() {
    private val mRetrofit: Retrofit

    val jsonApi: SmscRequest
        get() = mRetrofit.create(SmscRequest::class.java)

    init {
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {

        private var mInstance: SmscService? = null
        private val BASE_URL = "https://smsc.ru"

        val instance: SmscService
            get() {
                if (mInstance == null) {
                    mInstance = SmscService()
                }
                return mInstance!!
            }
    }


}
