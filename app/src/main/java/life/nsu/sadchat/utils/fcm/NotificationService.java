package life.nsu.sadchat.utils.fcm;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAssNfVIM:APA91bHrAA0jprZzcu0YNSi-DTymYBkcyH8Q1b4qrxepl8gHYu2A0Atua4HrKT-Fn3D1YV_lNpkT1lTvL88LY1X0CiGl1bd6QGbDGDN643UD3v1TWZEKoH2y2gS_OU6RFF5ItO9GsyfO"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);
}
