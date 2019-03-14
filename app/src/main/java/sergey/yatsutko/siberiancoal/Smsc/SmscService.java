package sergey.yatsutko.siberiancoal.Smsc;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SmscService {

    private static SmscService mInstance;
    private static final String BASE_URL = "https://smsc.ru";
    private Retrofit mRetrofit;

    private SmscService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static SmscService getInstance() {
        if (mInstance == null) {
            mInstance = new SmscService();
        }
        return mInstance;
    }

    public SmscRequest getJSONApi() {
        return mRetrofit.create(SmscRequest.class);
    }

    public void SendSms (String message, String phone) {
        SmscService.getInstance()
                .getJSONApi()
                .sendSms(
                        "lflagmanl",
                        "eujkmkexitdct[!",
                        phone,
                        message
                ).enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {

            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {

            }
        });
    }


}
