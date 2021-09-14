package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class UpdateInfoActivity extends AppCompatActivity {

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @BindView(R.id.edit_user_name)
    EditText edt_user_name;
    @BindView(R.id.edit_user_email)
    EditText edt_user_email;
    @BindView(R.id.btn_update)
    Button btn_update;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        ButterKnife.bind(this);
        init();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        toolbar.setTitle(getString(R.string.update_information));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){

                    if(TextUtils.isEmpty(edt_user_email.getText()))
                        Toast.makeText(UpdateInfoActivity.this, "Enter Email Id", Toast.LENGTH_SHORT).show();
                    else if(TextUtils.isEmpty(edt_user_name.getText()))
                        Toast.makeText(UpdateInfoActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    else {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("email", edt_user_email.getText().toString());
                        jsonObject.addProperty("name", edt_user_name.getText().toString());
                        jsonObject.addProperty("id", user.getUid());
                        jsonObject.addProperty("phone", user.getPhoneNumber().substring(3));

                        compositeDisposable.add(myRestaurantAPI.registerUser(jsonObject)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(registerModel -> {
                                            if (!registerModel.getErr()) {
                                                JsonObject jsonObject1 = new JsonObject();
                                                jsonObject1.addProperty("phone" , user.getPhoneNumber().substring(3));

                                                compositeDisposable.add(myRestaurantAPI.getUserByPhone(jsonObject1)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(myUser -> {
                                                            if(!myUser.getErr()) {
                                                                Common.myCurrentUser = myUser.getUser();
                                                                Intent intent = new Intent(UpdateInfoActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        } , throwable -> {
                                                            Toast.makeText(UpdateInfoActivity.this, "Error registering", Toast.LENGTH_SHORT).show();
                                                        })
                                                );
                                            }
                                        },
                                        throwable -> {
                                            dialog.dismiss();
                                            Toast.makeText(UpdateInfoActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                        );
                    }
                }
                else{
                    Toast.makeText(UpdateInfoActivity.this, "Not signed In! Please sign in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateInfoActivity.this , MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
    }
}