package com.alam.eathub;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alam.eathub.Common.Common;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    @BindView(R.id.zxscan)
    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scana_qr);

        initView();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        zXingScannerView.setResultHandler(ScanQRActivity.this);
                        zXingScannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void onStop() {
        zXingScannerView.stopCamera();
        super.onStop();
    }

    @Override
    public void handleResult(Result rawResult) {
        //Return intent
        Intent intent = new Intent();
        intent.putExtra(Common.QR_CODE_TAG , rawResult.getText());
        setResult(Activity.RESULT_OK , intent);
        finish();
    }
}