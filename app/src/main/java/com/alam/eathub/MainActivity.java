package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  static  final  int APP_REQUEST_CODE = 1234;

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;


    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;

    @OnClick(R.id.btn_sign_in)
    void loginUser(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build() , APP_REQUEST_CODE);
    }

    @BindView(R.id.btn_sign_in_gmail)
    Button btn_sign_in_gmail;

    //Todo - make google signin
    @OnClick(R.id.btn_sign_in_gmail)
    void skip(){
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode != RESULT_OK){
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
        //befre OnSuccess
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Paper.init(this);
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuth = FirebaseAuth.getInstance();

        listener = firebaseAuth1 -> {
            FirebaseUser user = firebaseAuth1.getCurrentUser();
            //if user already logged in
            if(user != null){
                dialog.show();

                FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(e -> {})
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
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
                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } , throwable -> {
                                            Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                );
                            }
                        }
                    }
                });
            }
        };
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(listener != null && firebaseAuth != null)
            firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener != null && firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }
}