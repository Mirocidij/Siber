package sergey.yatsutko.siberiancoal.Smsc;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SmscRequest {
    @GET("/sys/send.php")
    Call<String[]> sendSms(
            @Query("login") String login,
            @Query("psw") String password,
            @Query("phones") String phoneNumber,
            @Query("mes") String message
    );
}

//?charset=utf-8&translit=0&