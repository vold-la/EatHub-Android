package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.GetKeyModel;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        Dexter.withActivity(this)
                .withPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.CAMERA})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        //Get Token
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if(task.isSuccessful()){

                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if(user != null){
                                                //qwer  fixed

                                                JsonObject jsonObject = new JsonObject();
                                                jsonObject.addProperty("phone" , user.getPhoneNumber().substring(3));
                                                compositeDisposable.add(myRestaurantAPI.getUserByPhone(jsonObject)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(myUser -> {
                                                                    if(!myUser.getErr()) {
                                                                        Common.myCurrentUser = myUser.getUser();
                                                                        Log.e("tg :", String.valueOf(myUser.getUser().getOrders().size()));
                                                                        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                } , throwable ->
                                                                {
                                                                    Intent intent = new Intent(SplashScreen.this, UpdateInfoActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                })
                                                );
                                            }
                                            else {
                                                Toast.makeText(SplashScreen.this, "Not signed In! Please sign in", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SplashScreen.this , MainActivity.class));
                                                finish();
                                            }
                                        }
                                    }
                                });
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        },3000);*/
    }

    private void init() {
        Paper.init(this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
    }
}