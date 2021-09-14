package com.alam.eathub.Retrofit;

import com.alam.eathub.Model.AddonModel;
import com.alam.eathub.Model.DiscountModel;
import com.alam.eathub.Model.EventBus.CreateOrderModel;
import com.alam.eathub.Model.FavoriteModel;
import com.alam.eathub.Model.FavoriteOnlyId;
import com.alam.eathub.Model.FavoriteOnlyIdModel;
import com.alam.eathub.Model.FoodModel;
import com.alam.eathub.Model.GetKeyModel;
import com.alam.eathub.Model.MaxOrderModel;
import com.alam.eathub.Model.MenuModel;
import com.alam.eathub.Model.My.MyyRestaurant;
import com.alam.eathub.Model.MyRestaurantModel;
import com.alam.eathub.Model.OrderModel;
import com.alam.eathub.Model.RestaurantModel;
import com.alam.eathub.Model.SizeModel;
import com.alam.eathub.Model.TokenModel;
import com.alam.eathub.Model.UpdateOrderModel;
import com.alam.eathub.Model.UpdateUserModel;
import com.alam.eathub.Model.UserModel;
import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    @GET("key")
    Observable<GetKeyModel> getKey(@Query("fbid") String fbid );

    @GET("discount")
    Observable<DiscountModel> getDiscount(@HeaderMap Map<String ,String> headers,
                                          @Query("code") String code);


    @GET("discount/check")
    Observable<DiscountModel> checkDiscount(@HeaderMap Map<String ,String> headers,
                                            @Query("code") String code);

    @GET("user")
    Observable<UserModel> getUser(@HeaderMap Map<String ,String> headers);

    @GET("restaurant")
    Observable<RestaurantModel> getRestaurant(@HeaderMap Map<String ,String> headers);

    @GET("restaurant/id")
    Observable<RestaurantModel> getRestaurantById(@HeaderMap Map<String ,String> headers,
                                                  @Query("restaurantId") String id);

    @GET("restaurant/nearby")
    Observable<RestaurantModel> getNearbyRestaurant(@HeaderMap Map<String ,String> headers,
                                                    @Query("lat") Double lat ,
                                                    @Query("lng") Double lng ,
                                                    @Query("distance") int distance);
    @GET("menu")
    Observable<MenuModel> getCategories(@HeaderMap Map<String ,String> headers,
                                        @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<FoodModel> getFoodOfMenu(@HeaderMap Map<String ,String> headers,
                                        @Query("menuId") int menuId);

    @GET("food/id")
    Observable<FoodModel> getFoodById(@HeaderMap Map<String ,String> headers,
                                        @Query("foodId") int foodId);

    @GET("food/search")
    Observable<FoodModel> searchFood(@HeaderMap Map<String ,String> headers,
                                     @Query("foodName") String foodName,
                                     @Query("menuId") int menuId);

    @GET("size")
    Observable<SizeModel> getSizeOfFood(@HeaderMap Map<String ,String> headers,
                                        @Query("foodId") int foodId);

    @GET("addon")
    Observable<AddonModel> getAddonOfFood(@HeaderMap Map<String ,String> headers,
                                               @Query("foodId") int foodId);

    @GET("favorite")
    Observable<FavoriteModel> getFavoriteByUser(@HeaderMap Map<String ,String> headers);

    @GET("favorite/restaurant")
    Observable<FavoriteOnlyIdModel> getFavoriteByRestaurant(@HeaderMap Map<String ,String> headers ,
                                                            @Query("restaurantId") int restaurantId);

    @GET("order")
    Observable<OrderModel> getOrder(@HeaderMap Map<String ,String> headers,
                                    @Query("from") int from ,
                                    @Query("to") int to);
    @GET("order/maxorder")
    Observable<MaxOrderModel> getMaxOrder(@HeaderMap Map<String ,String> headers );

    @GET("token")
    Observable<TokenModel> getToken(@HeaderMap Map<String ,String> headers);

    //POST

    @POST("discount/apply")
    @FormUrlEncoded
    Observable<DiscountModel> insertDiscount(@HeaderMap Map<String ,String> headers ,
                                               @Field("code") String code );

    @POST("token")
    @FormUrlEncoded
    Observable<TokenModel> updateTokenToServer(@HeaderMap Map<String ,String> headers,
                                       @Field("token") String token );


    @POST("order")
    @FormUrlEncoded
    Observable<CreateOrderModel> createOrder(@HeaderMap Map<String ,String> headers,
                                    @Field("orderPhone") String orderPhone ,
                                    @Field("orderName") String orderName ,
                                    @Field("orderAddress") String orderAddress ,
                                    @Field("orderDate") String orderDate ,
                                    @Field("restaurantId") int restaurantId ,
                                    @Field("transactionId") String transactionId ,
                                    @Field("cod") boolean cod ,
                                    @Field("totalPrice") Double totalPrice ,
                                    @Field("numOfItem") int numOfItem );
    @POST("order")
    @FormUrlEncoded
    Observable<UpdateOrderModel> updateOrder(@HeaderMap Map<String ,String> headers,
                                             @Field("orderId") String orderId ,
                                             @Field("orderDetail") String orderDetail);


    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@HeaderMap Map<String ,String> headers,
                                               @Field("userPhone") String userPhone ,
                                               @Field("userName") String userName ,
                                               @Field("userAddress") String userAddress );

    @POST("favorite")
    @FormUrlEncoded
    Observable<FavoriteModel> insertFavorite(@HeaderMap Map<String ,String> headers,
                                             @Field("foodId") int foodId ,
                                             @Field("restaurantId") int restaurantId ,
                                             @Field("restaurantName") String restaurantName ,
                                             @Field("foodName") String foodName ,
                                             @Field("foodImage") String foodImage ,
                                             @Field("price") double price);

    //Delete
    @DELETE("favorite")
    Observable<FavoriteModel> removeFavorite(@HeaderMap Map<String ,String> headers,
                                             @Query("foodId") int foodId ,
                                             @Query("restaurantId") int restaurantId);
}
