package life.nsu.sadchat.utils.fcm;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAssNfVIM:APA91bFjF1pmapR_N6-Xi5nso_GGxkBeF0iOq4kQdaxtFSuHjq79LkIqTQybBtQD6JZR8ebL625HTQlIFFXJfdFE06y0gNQqiv5U15KVlD8d138UJKBq_gaYP2cNWNK7s43fjYrAXTod"
            }
    )

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RequestBody body);
}
