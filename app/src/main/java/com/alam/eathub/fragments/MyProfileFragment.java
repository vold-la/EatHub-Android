package com.alam.eathub.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alam.eathub.Common.Common;
import com.alam.eathub.HomeActivity;
import com.alam.eathub.MainActivity;
import com.alam.eathub.R;
import com.alam.eathub.UpdateInfoActivity;
import com.alam.eathub.ViewOrderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView mLogOutText,mUserProfileName,mUserProfileNum,mUserProfileAddress;
    private ImageView mMyOrdersText;
    private ImageView mChangeAddressView;
    //private CircleImageView mUserProfileImage;
    private ProgressDialog mProgressDialog;
    private StorageReference mUserImageRef;
    private StorageReference filePath;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        init();
        getUserInfo();
        mLogOutText.setOnClickListener(this);
        mMyOrdersText.setOnClickListener(this);
        //mUserProfileImage.setOnClickListener(this);
        mChangeAddressView.setOnClickListener(this);

        return view;
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getContext());
      //  db = FirebaseFirestore.getInstance();
        mLogOutText = view.findViewById(R.id.logOutText);
        mUserProfileName = view.findViewById(R.id.userProfileName);
        mUserProfileNum = view.findViewById(R.id.userProfileNumber);
        mMyOrdersText = view.findViewById(R.id.myOrdersImage);
        mChangeAddressView = view.findViewById(R.id.changeAddressImage);
        mUserProfileAddress = view.findViewById(R.id.userProfileAddress);
        //mUserProfileImage = view.findViewById(R.id.userProfileImage);

    }

    private void getUserInfo() {
                //String imageRef = (String) Objects.requireNonNull(docRef).get("user_profile_image");
                String userName = (String) Common.myCurrentUser.getName();
                String userPhoneNum = (String) Common.myCurrentUser.getPhone();
            //    String userAddress = (String) Common.myCurrentUser.getAddress().get(0).getAddress();

                mUserProfileName.setText(userName);
                mUserProfileNum.setText(userPhoneNum);
              //  if(!userAddress.isEmpty())
                //    mUserProfileAddress.setText(userAddress);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.userProfileImage:
                //SignOut();
                break;

            case R.id.logOutText:
                SignOut();
                break;

            case R.id.myOrdersImage:
                Intent intent = new Intent(getActivity(), ViewOrderActivity.class);
                startActivity(intent);
                break;

            case R.id.changeAddressImage:
                Intent intent1 = new Intent(getActivity(), UpdateInfoActivity.class);
                intent1.putExtra("INT", "THREE");
                startActivity(intent1);
                break;

        }
    }

    private  void SignOut(){

        AlertDialog confirmDialog = new AlertDialog.Builder(requireActivity())
                .setTitle("SignOut")
                .setMessage("Do you really want to Sign Out?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Common.currentUser = null;
                        Common.currentRestaurant = null;

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();

                    }
                })
                .create();

        confirmDialog.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    //    imagePicker.handleActivityResult(resultCode, requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      //  imagePicker.handlePermission(requestCode, grantResults);
    }
/*
    public void showAll() {
        refreshImagePicker();
        imagePicker.choosePicture(false);
    }

    private void refreshImagePicker() {
        imagePicker = new ImagePicker(getActivity(),
                this,
                imageUri -> {
                    mProgressDialog.setMessage("Uploading, Please wait...");
                    mProgressDialog.show();
                    mUserProfileImage.setImageURI(imageUri);
                    mImageUri = imageUri;
                    filePath = mUserImageRef.child("user_profile_image").child(uid + ".jpg");
                    filePath.putFile(mImageUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mUserImageRef.child("user_profile_image").child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                                final String downloadUrl = uri.toString();
                                UploadTask uploadTask = filePath.putFile(mImageUri);
                                uploadTask.addOnSuccessListener(taskSnapshot -> {
                                    if (task.isSuccessful()){
                                        HashMap<String,Object> updateHashmap = new HashMap<>();
                                        updateHashmap.put("user_profile_image", downloadUrl);
                                        mUserRef.update(updateHashmap).addOnSuccessListener(o ->
                                                mProgressDialog.dismiss());
                                    }
                                });
                            });
                        }
                    });
                });
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    */
}