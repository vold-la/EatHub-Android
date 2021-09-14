package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.BraintreeToken;
import com.alam.eathub.Model.BraintreeTransaction;
import com.alam.eathub.Model.Discount;
import com.alam.eathub.Model.EventBus.CreateOrderModel;
import com.alam.eathub.Model.EventBus.SendTotalCashEvent;
import com.alam.eathub.Model.FCMSendData;
import com.alam.eathub.Retrofit.IBraintreeAPI;
import com.alam.eathub.Retrofit.IFCMService;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetroFitFCMClient;
import com.alam.eathub.Retrofit.RetrofitBraintreeClient;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_BRAINTREE_CODE = 7777;
    private static final int SCAN_QR_PERMISSION = 8888;
    boolean isUseDiscount  = false;
    String discountUsed ="";

    @BindView(R.id.btn_scan_qr)
    Button btn_scan_qr;
    @BindView(R.id.btn_apply)
    Button btn_apply;
    @BindView(R.id.edt_discount_code)
    EditText edt_discount_code;
    @BindView(R.id.edt_date)
    EditText edt_date;
    @BindView(R.id.txt_discount_cash)
    TextView txt_discount_cash;
    @BindView(R.id.txt_total_cash)
    TextView txt_total_cash;
    @BindView(R.id.txt_user_phone)
    TextView txt_user_phone;
    @BindView(R.id.txt_user_address)
    TextView txt_user_address;
    @BindView(R.id.txt_new_address)
    TextView txt_new_address;
    @BindView(R.id.btn_add_new_address)
    Button btn_add_new_address;
    @BindView(R.id.ckb_default_address)
    CheckBox ckb_default_address;
    @BindView(R.id.rdi_cod)
    RadioButton rdi_cod;
    @BindView(R.id.rdi_online_payment)
    RadioButton rdi_online_payment;
    @BindView(R.id.btn_proceed)
    Button btn_proceed;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IFCMService ifcmService;
    IMyRestaurantAPI myRestaurantAPI;
    IBraintreeAPI braintreeAPI;
    AlertDialog dialog;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    boolean isSelectedDate = false, isAddNewAddress = false;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        init();
        initView();
    }

    private void init() {
        ifcmService = RetroFitFCMClient.getInstance().create(IFCMService.class);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        braintreeAPI = RetrofitBraintreeClient.getInstance(Common.API_PAYMENT_ENDPOINT).create(IBraintreeAPI.class);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    private void initView() {
        ButterKnife.bind(this);

        txt_user_phone.setText(Common.currentUser.getUserPhone());
        txt_user_address.setText(Common.currentUser.getAddress());

        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_scan_qr.setOnClickListener(view -> {
            startActivityForResult(new Intent(PlaceOrderActivity.this , ScanQRActivity.class) , SCAN_QR_PERMISSION);
        });

        btn_apply.setOnClickListener(view ->{
            dialog.show();

            Map<String , String> headers = new HashMap<>();
            headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
            compositeDisposable.add(myRestaurantAPI.checkDiscount(headers , edt_discount_code.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(checkDiscountModel ->{
                        if(checkDiscountModel.isSuccess()){

                            compositeDisposable.add(myRestaurantAPI.getDiscount(headers ,  edt_discount_code.getText().toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(discountModel -> {
                                        if(discountModel.isSuccess()){
                                            if(!isUseDiscount){

                                                Discount discount = discountModel.getResult().get(0);
                                                Double total_cash = Double.valueOf(txt_total_cash.getText().toString());
                                                Double discount_value = (total_cash * discount.getValue())/100;
                                                Double final_cash = total_cash - discount_value;

                                                txt_discount_cash.setText(new StringBuilder("(-")
                                                        .append(discount.getValue())
                                                        .append("%)"));
                                                txt_discount_cash.setVisibility(View.VISIBLE);

                                                txt_total_cash.setText(new StringBuilder("").append(final_cash));

                                                isUseDiscount = true;
                                                discountUsed = edt_discount_code.getText().toString();
                                            }
                                            else{
                                                Toast.makeText(this, "code applied already", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        dialog.dismiss();
                                    } , throwable -> {
                                        dialog.dismiss();
                                        Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                            );

                        }
                        else {
                            Toast.makeText(this, "You already used this code", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }  , throwable -> {
                        Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
            );
        });

        btn_add_new_address.setOnClickListener(view -> {
            isAddNewAddress = true;
            ckb_default_address.setChecked(false);
            View add_new_address = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.add_new_address, null);
            EditText edt_new_address = (EditText) add_new_address.findViewById(R.id.edt_add_new_address);
            edt_new_address.setText(txt_new_address.getText().toString());

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(add_new_address)
                    .setNegativeButton("CANCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .setPositiveButton("ADD", ((dialogInterface, i) -> txt_new_address.setText(edt_new_address.getText().toString())));

            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
            addNewAddressDialog.show();
        });

        edt_date.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            dpd.show(getSupportFragmentManager(), "DatePickerDialog");
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectedDate) {
                    Toast.makeText(PlaceOrderActivity.this, "Please Select Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    String dateString = edt_date.getText().toString();
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        Date orderDate = df.parse(dateString);
                        //get current date
                        Calendar calendar = Calendar.getInstance();
                        Date currentDate = df.parse(df.format(calendar.getTime()));

                        if(!DateUtils.isToday(orderDate.getTime())) {
                            if (orderDate.before(currentDate)) {
                                Toast.makeText(PlaceOrderActivity.this, "Please choose current date or future date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!isAddNewAddress) {
                    if (!ckb_default_address.isChecked()) {
                        Toast.makeText(PlaceOrderActivity.this, "Please Select default address or set new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (rdi_cod.isChecked()) {
                    getOrderNumber(false);
                } else if (rdi_online_payment.isChecked()) {
                    getOrderNumber(true);
                }
            }
        });
    }

    private void getOrderNumber(boolean b) {
        dialog.show();

        if (!b) {
            String address = ckb_default_address.isChecked() ? txt_user_address.getText().toString() : txt_new_address.getText().toString();

            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {
                                //Get Order Number from Server
                                Map<String , String> headers = new HashMap<>();
                                headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                                compositeDisposable.add(myRestaurantAPI.createOrder(headers ,
                                        Common.currentUser.getUserPhone(),
                                        Common.currentUser.getName(),
                                        address,
                                        edt_date.getText().toString(),
                                        Common.currentRestaurant.getId(),
                                        "NONE",
                                        true,
                                        Double.valueOf(txt_total_cash.getText().toString()),
                                        cartItems.size())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(createOrderModel -> {
                                                    if (createOrderModel.isSuccess()) {
                                                        //After getting order number , update all item of this order to orderdetail
                                                        //First select Cart Items
                                                        compositeDisposable.add(myRestaurantAPI.updateOrder(headers,
                                                                String.valueOf(createOrderModel.getResult().get(0).getOrderNumber()),
                                                                new Gson().toJson(cartItems))
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(updateOrderModel -> {
                                                                    if (updateOrderModel.isSuccess()) {
                                                                        //Before clean we  will apply discount
                                                                        if(isUseDiscount && !TextUtils.isEmpty(discountUsed)){

                                                                            compositeDisposable.add(myRestaurantAPI.insertDiscount(headers , discountUsed)
                                                                                    .subscribeOn(Schedulers.io())
                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                    .subscribe(discountModel -> {
                                                                                        if(discountModel.isSuccess()){
                                                                                            cartDataSource.cleanCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                                                                    .subscribeOn(Schedulers.io())
                                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                                    .subscribe(new SingleObserver<Integer>() {
                                                                                                        @Override
                                                                                                        public void onSubscribe(Disposable d) {

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onSuccess(Integer integer) {
                                                                                                            //Create notification
                                                                                                            Map<String , String> dataSend = new HashMap<>();
                                                                                            /*dataSend.put(Common.NOTIFI_TITLE , "New Order");
                                                                                            dataSend.put(Common.NOTIFI_CONTENT , "You have new order"+createOrderModel.getResult().get(0).getOrderNumber());

                                                                                            FCMSendData sendData = new FCMSendData(Common.createTopicSender(Common.getTopicChannel(Common.currentRestaurant.getId())) , dataSend);
                                                                                            compositeDisposable.add(ifcmService.sendNotification(sendData)
                                                                                                    .observeOn(Schedulers.io())
                                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                                    .subscribe(fcmResponse -> {*/
                                                                                                            Toast.makeText(PlaceOrderActivity.this, "order Placed", Toast.LENGTH_SHORT).show();
                                                                                                            Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                            homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                            startActivity(homeActivity);
                                                                                                            finish();
                                                                                                    /*} , throwable -> {
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "order Placed but cant send notification to server", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeActivity);
                                                                                                        finish();
                                                                                                    })
                                                                                            );*/
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onError(Throwable e) {
                                                                                                            Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    } , throwable -> {
                                                                                        Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    })
                                                                            );
                                                                        }
                                                                        else{
                                                                            //After updated item  ,clear cart and message update
                                                                            cartDataSource.cleanCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                                                    .subscribeOn(Schedulers.io())
                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                    .subscribe(new SingleObserver<Integer>() {
                                                                                        @Override
                                                                                        public void onSubscribe(Disposable d) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onSuccess(Integer integer) {
                                                                                            //Create notification
                                                                                            Map<String , String> dataSend = new HashMap<>();
                                                                                            /*dataSend.put(Common.NOTIFI_TITLE , "New Order");
                                                                                            dataSend.put(Common.NOTIFI_CONTENT , "You have new order"+createOrderModel.getResult().get(0).getOrderNumber());

                                                                                            FCMSendData sendData = new FCMSendData(Common.createTopicSender(Common.getTopicChannel(Common.currentRestaurant.getId())) , dataSend);
                                                                                            compositeDisposable.add(ifcmService.sendNotification(sendData)
                                                                                                    .observeOn(Schedulers.io())
                                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                                    .subscribe(fcmResponse -> {*/
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "order Placed", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeActivity);
                                                                                                        finish();
                                                                                                    /*} , throwable -> {
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "order Placed but cant send notification to server", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeActivity);
                                                                                                        finish();
                                                                                                    })
                                                                                            );*/
                                                                                        }

                                                                                        @Override
                                                                                        public void onError(Throwable e) {
                                                                                            Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });

                                                                        }
                                                                    }

                                                                    if (dialog.isShowing())
                                                                        dialog.dismiss();

                                                                }, throwable -> {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(this, "[UPDATE ORDER]", Toast.LENGTH_SHORT).show();
                                                                }));
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(this, "[CREATE ORDER]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , throwable -> {
                                                    dialog.dismiss();
                                                    Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }));
                            }
                            , throwable -> {
                                Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
        }
        else {
            //if online payment
            //first get token
            compositeDisposable.add(braintreeAPI.getToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(braintreeToken -> {
                        if (braintreeToken.isSuccess()) {
                            Log.d("success",""+braintreeToken.getClientToken());
                            DropInRequest dropInRequest = new DropInRequest().clientToken(braintreeToken.getClientToken());
                            startActivityForResult(dropInRequest.getIntent(PlaceOrderActivity.this), REQUEST_BRAINTREE_CODE);
                        } else {
                            Toast.makeText(PlaceOrderActivity.this, "Cannot get token", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }, throwable -> {
                                dialog.dismiss();
                                Toast.makeText(PlaceOrderActivity.this, "[GET TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                    ));
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;

        edt_date.setText(new StringBuilder("")
                .append(monthOfYear + 1) // +1  because calender will return 0~11
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_BRAINTREE_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();

                //After having nonce , make payment with API
                if (!TextUtils.isEmpty(txt_total_cash.getText().toString())) {
                    String amount = txt_total_cash.getText().toString();

                    if (!dialog.isShowing())
                        dialog.show();

                    String address = ckb_default_address.isChecked() ? txt_user_address.getText().toString() : txt_new_address.getText().toString();
                    compositeDisposable.add(braintreeAPI.submitPayment(amount, nonce.getNonce())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BraintreeTransaction>() {
                                           @Override
                                           public void accept(BraintreeTransaction braintreeTransaction) throws Exception {
                                               if (braintreeTransaction.isSuccess()) {
                                                   if (!dialog.isShowing())
                                                       dialog.show();
                                                   //After we have transaction , just make order like COD Payment
                                                   compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                           .subscribeOn(Schedulers.io())
                                                           .observeOn(AndroidSchedulers.mainThread())
                                                           .subscribe(cartItems -> {
                                                                       //Get Order Number from Server
                                                                       Map<String , String> headers = new HashMap<>();
                                                                       headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                                                                       compositeDisposable.add(myRestaurantAPI.createOrder(headers ,
                                                                               Common.currentUser.getUserPhone(),
                                                                               Common.currentUser.getName(),
                                                                               address,
                                                                               edt_date.getText().toString(),
                                                                               Common.currentRestaurant.getId(),
                                                                               braintreeTransaction.getTransaction().getId(),
                                                                               false,
                                                                               Double.valueOf(amount),
                                                                               cartItems.size())
                                                                               .subscribeOn(Schedulers.io())
                                                                               .observeOn(AndroidSchedulers.mainThread())
                                                                               .subscribe(createOrderModel -> {
                                                                                           if (createOrderModel.isSuccess()) {
                                                                                               //After getting order number , update all item of this order to orderdetail
                                                                                               //First select Cart Items
                                                                                               compositeDisposable.add(myRestaurantAPI.updateOrder(headers,
                                                                                                       String.valueOf(createOrderModel.getResult().get(0).getOrderNumber()),
                                                                                                       new Gson().toJson(cartItems))
                                                                                                       .subscribeOn(Schedulers.io())
                                                                                                       .observeOn(AndroidSchedulers.mainThread())
                                                                                                       .subscribe(updateOrderModel -> {
                                                                                                           if (updateOrderModel.isSuccess()) {
                                                                                                               //After updated item  ,clear cart and message update
                                                                                                               cartDataSource.cleanCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                                                                                       .subscribeOn(Schedulers.io())
                                                                                                                       .observeOn(AndroidSchedulers.mainThread())
                                                                                                                       .subscribe(new SingleObserver<Integer>() {
                                                                                                                           @Override
                                                                                                                           public void onSubscribe(Disposable d) {

                                                                                                                           }

                                                                                                                           @Override
                                                                                                                           public void onSuccess(Integer integer) {
                                                                                                                               //Create notification
                                                                                                                               Map<String , String> dataSend = new HashMap<>();
                                                                                            /*dataSend.put(Common.NOTIFI_TITLE , "New Order");
                                                                                            dataSend.put(Common.NOTIFI_CONTENT , "You have new order"+createOrderModel.getResult().get(0).getOrderNumber());

                                                                                            FCMSendData sendData = new FCMSendData(Common.createTopicSender(Common.getTopicChannel(Common.currentRestaurant.getId())) , dataSend);
                                                                                            compositeDisposable.add(ifcmService.sendNotification(sendData)
                                                                                                    .observeOn(Schedulers.io())
                                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                                    .subscribe(fcmResponse -> {*/
                                                                                                                               Toast.makeText(PlaceOrderActivity.this, "order Placed", Toast.LENGTH_SHORT).show();
                                                                                                                               Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                                               homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                                               startActivity(homeActivity);
                                                                                                                               finish();
                                                                                                    /*} , throwable -> {
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "order Placed but cant send notification to server", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeActivity);
                                                                                                        finish();
                                                                                                    })
                                                                                            );*/
                                                                                                                           }

                                                                                                                           @Override
                                                                                                                           public void onError(Throwable e) {
                                                                                                                               Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                           }
                                                                                                                       });
                                                                                                           } // Todo paste from line 303

                                                                                                           if (dialog.isShowing())
                                                                                                               dialog.dismiss();

                                                                                                       }, throwable -> {
                                                                                                           dialog.dismiss();
                                                                                                           Toast.makeText(PlaceOrderActivity.this, "[UPDATE ORDER]", Toast.LENGTH_SHORT).show();
                                                                                                       }));
                                                                                           }

                                                                                           else {
                                                                                               dialog.dismiss();
                                                                                               Toast.makeText(PlaceOrderActivity.this, "[CREATE ORDER]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                           }
                                                                                       }
                                                                                       , throwable -> {
                                                                                           dialog.dismiss();
                                                                                           Toast.makeText(PlaceOrderActivity.this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                       }));
                                                                   }
                                                                   , throwable -> {
                                                                       Toast.makeText(PlaceOrderActivity.this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                   }));
                                               }
                                               else {
                                                   dialog.dismiss();
                                                   Toast.makeText(PlaceOrderActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }, throwable -> {
                                        if (!dialog.isShowing())
                                            dialog.show();
                                        Toast.makeText(this, "[SUBMIT PAYMENT]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        }
        else if(requestCode == SCAN_QR_PERMISSION){
            if(resultCode == RESULT_OK){
                edt_discount_code.setText(data.getStringExtra(Common.QR_CODE_TAG));
                //Enable button apply
                btn_apply.setEnabled(true);
            }
        }
    }

    //Event Bus
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //since in MyAdapter we use postSticky ,so this event need set sticky too
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvent event) {
        txt_total_cash.setText(String.valueOf(event.getCash()));
    }
}