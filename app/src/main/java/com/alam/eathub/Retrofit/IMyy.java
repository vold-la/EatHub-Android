package com.alam.eathub.Retrofit;

import com.alam.eathub.Model.DiscountModel;
import com.alam.eathub.Model.My.CityId;
import com.alam.eathub.Model.My.MyyRestaurant;
import com.alam.eathub.Model.My.OrderId;
import com.alam.eathub.Model.My.PushOrder;
import com.alam.eathub.Model.My.RegisterModel;
import com.alam.eathub.Model.My.SearchedRestaurant;
import com.alam.eathub.Model.MyUser.MyUser;
import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyy {

    @Headers({"Content-Type:application/json"})
    @GET("users/orderId")
    Observable<OrderId> getOrderId(@Query("amount") int amount);

    @Headers({"Content-Type:application/json"})
    @GET("search/restaurant")
    Observable<SearchedRestaurant> getSearchedRestaurant(@Query("q") String q,
                                                         @Query("city_id") int city_id);

    @Headers({"Content-Type:application/json"})
    @POST("search/cityId")
    Observable<CityId> getCityId(@Body JsonObject jsonObject);

    @Headers({"Content-Type:application/json"})
    @POST("users/getUserByPhone")
    Observable<MyUser> getUserByPhone(@Body JsonObject jsonObject);

    @Headers({"Content-Type:application/json"})
    @POST("auth/registerPhone")
    Observable<RegisterModel> registerUser(@Body JsonObject jsonObject);

    @Headers({"Content-Type:application/json"})
    @POST("restaurant/filterd")
    Observable<MyyRestaurant> getMyRestaurant(@Body JsonObject jsonObject);


    @Headers({"Content-Type:application/json"})
    @POST("users/captureCod")
    Observable<PushOrder> pushOrder(@Body JsonObject jsonObject);


}
