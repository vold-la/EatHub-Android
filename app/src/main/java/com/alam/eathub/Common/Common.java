package com.alam.eathub.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.alam.eathub.Model.Addon;
import com.alam.eathub.Model.Favorite;
import com.alam.eathub.Model.FavoriteOnlyId;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.Model.MyRestaurant;
import com.alam.eathub.Model.MyUser.MyUser;
import com.alam.eathub.Model.Restaurant;
import com.alam.eathub.Model.User;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.GoogleRetrofitClient;
import com.alam.eathub.Retrofit.IFCMService;
import com.alam.eathub.Retrofit.IGoogleService;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.alam.eathub.Retrofit.Services.MyFirebaseMessagingService;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.core.app.NotificationCompat;

public class Common {
    public static final String API_RESTAURANT_ENDPOINT = "https://eathub-backend.herokuapp.com/api/v1/";
    public static final String API_RESTAURANT_ENDPOINT_TEST = "https://eathub-nbackend.herokuapp.com/api/";
   // public static final String API_RESTAURANT_ENDPOINT_TEST = "http://192.168.2.114:5000/api/";
    public static final String API_PAYMENT_ENDPOINT = "https://eathub-payment.herokuapp.com/";
    public static final String RAZOR_PAY_KEY_ID="rzp_test_mZXDpbDRz5P2t0";
    public static String API_KEY = "";
    public static String MAPBOX_API_KEY = "";
    public static String CITY_TITLE = "";
    public static int REQUEST_CODE_AUTOCOMPLETE = 121;
    public static int CITY_ID;
    public static final int DEFAULT_COLUMN_TYPE = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String REMEMBER_FBID = "REMEMBER_FBID";
    public static final String API_KEY_TAG = "API_KEY";
    public static final String NOTIFI_TITLE = "title";
    public static final String NOTIFI_CONTENT = "content";
    public static final String QR_CODE_TAG = "QR_CODE";
    public static User currentUser;
    public static com.alam.eathub.Model.MyUser.User myCurrentUser;
    public Rrestaurant searchedRestaurant;
    public static Restaurant currentRestaurant;
    public static Rrestaurant myCurrentRestaurant;
    public static Set<Addon> addonList = new HashSet<>();
    public static List<FavoriteOnlyId> currentFavOfRestaurant;
    private static JsonObject ALL_FILTERS = new JsonObject();
    private static JsonObject FILTERS = new JsonObject();
    public static  String SORT ;
    public static  int RATINGS ;
    public static  int COST ;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";


    public static JsonObject getALL_FILTERS() {
        return ALL_FILTERS;
    }

    public static void setALL_FILTERS(JsonObject ALL_FILTER) {
        ALL_FILTERS = ALL_FILTER;
    }

    public static JsonObject getFILTERS() {
        return FILTERS;
    }

    public static void setFILTERS(JsonObject FILTER) {
        FILTERS = FILTER;
    }

    public static String currentKey;
    public static final String SHIPPER_INFO_TABLE = "ShippingOrders";
    public static String DISTANCE= "";
    public static String DURATION= "";
    public static String ESTIMATED_TIME = "";


    public static IFCMService getFCMService(){
        return RetrofitClient.getInstance("https://fcm.googleapis.com/").create(IFCMService.class);
    }

    //food
    public static IGoogleService getGoogleMapAPI(){

        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }


    public static boolean checkFavorite(int id) {
        boolean result = false;
        for (FavoriteOnlyId item:currentFavOfRestaurant)
            if(item.getFoodId() == id)
                result = true;
        return result;
    }

    public static void removeFavorite(int id) {

        for (FavoriteOnlyId item:currentFavOfRestaurant)
            if(item.getFoodId() == id)
                currentFavOfRestaurant.remove(item);
    }

    public static String convertStatusToString(int orderStatus) {
        switch (orderStatus){
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Cancelled";
        }
    }

    public static void showNotification(Context context, int notiId, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,notiId,intent , PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "felngss_eathub";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel  = new NotificationChannel(NOTIFICATION_CHANNEL_ID , "EatHub Notification" , NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("EatHub User App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context , NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));

        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification mNotification = builder.build();

        notificationManager.notify(notiId , mNotification);

    }

    public static String getTopicChannel(int id) {
        return  new StringBuilder("Restaurant_").append(id).toString();
    }

    public static String createTopicSender(String topicChannel) {
        return  new StringBuilder("/topics/").append(topicChannel).toString();
    }

    public static String buildJWT(String apiKey) {
        return new StringBuilder("Bearer").append(" ").append(apiKey).toString();
    }
}
