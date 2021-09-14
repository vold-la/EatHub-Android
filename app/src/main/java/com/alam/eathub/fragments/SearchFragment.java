package com.alam.eathub.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyFavoriteAdapter;
import com.alam.eathub.Adapter.RestaurantSearchAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private View view;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mSearchRecycler;
    RestaurantSearchAdapter adapter;
    EditText searchEditText;

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        init();
        searchRestro();
        return view;
    }

    private void searchRestro() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 2){
                    compositeDisposable.clear();
                    compositeDisposable = new CompositeDisposable();
                    Log.e("res res  :" , String.valueOf(s));
                    compositeDisposable.add(myRestaurantAPI.getSearchedRestaurant(String.valueOf(s),Common.CITY_ID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(searchedRestaurant -> {
                                if(searchedRestaurant.getRestaurant() != null) {
                                    Log.e("res res : " , String.valueOf(searchedRestaurant.getRestaurant().size()));
                                    adapter = new RestaurantSearchAdapter(getContext(), searchedRestaurant.getRestaurant());
                                    mSearchRecycler.setAdapter(adapter);
                                }
                            })
                    );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void init() {
        dialog = new SpotsDialog.Builder().setContext(requireActivity()).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);

        searchEditText=view.findViewById(R.id.searchEditText);
        mSearchRecycler = view.findViewById(R.id.searchItemRecyclerView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        mSearchRecycler.setLayoutManager(linearLayoutManager);
        mSearchRecycler.setHasFixedSize(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}