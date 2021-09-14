package com.alam.eathub.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyFavoriteAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;

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

public class FavouriteFragment extends Fragment {

    private View view;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mFavoriteResRecyclerView;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    MyFavoriteAdapter adapter;

    @BindView(R.id.recycler_fav)
    RecyclerView recycle_fav;

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (adapter != null)
            adapter.onDestroy();
        super.onDestroy();
    }

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite, container, false);

        init();
        loadFavoriteItems();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        ImageView mGoBackBtn = view.findViewById(R.id.cartBackBtn);
        mGoBackBtn.setOnClickListener(view -> {
            Fragment fragment = new RestaurantFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        dialog = new SpotsDialog.Builder().setContext(requireActivity()).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        TextView mFavoriteResToolBarText = view.findViewById(R.id.confirmOrderText);
        mFavoriteResToolBarText.setText("Your Favorite Restaurants");
        //   db = FirebaseFirestore.getInstance();
        mFavoriteResRecyclerView = view.findViewById(R.id.recycler_fav);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mFavoriteResRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void loadFavoriteItems() {
        dialog.show();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Common.buildJWT(Common.API_KEY));
        compositeDisposable.add(myRestaurantAPI.getFavoriteByUser(headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteModel -> {
                            if (favoriteModel.isSuccess()) {
                                adapter = new MyFavoriteAdapter(requireActivity(), favoriteModel.getResult());
                                recycle_fav.setAdapter(adapter);
                            } else {
                                if (favoriteModel.getMessage().contains("Empty"))
                                    Toast.makeText(requireActivity(), "You don't have any favorite item", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(requireActivity(), "[GET FAV RESULT]" + favoriteModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        },
                        throwable -> {
                            dialog.dismiss();
                            Toast.makeText(requireActivity(), "[GET FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );

    }
}
/*
    private void getFavoriteRes() {
        Query query = db.collection("UserList").document(uid).collection("FavoriteRestaurants");
        FirestoreRecyclerOptions<FavoriteRestaurantDetails> favResModel = new FirestoreRecyclerOptions.Builder<FavoriteRestaurantDetails>()
                .setQuery(query, FavoriteRestaurantDetails.class)
                .build();
        FirestoreRecyclerAdapter<FavoriteRestaurantDetails, FavoriteResMenuHolder> adapter = new FirestoreRecyclerAdapter<FavoriteRestaurantDetails, FavoriteResMenuHolder>(favResModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull FavoriteResMenuHolder holder, int i, @NonNull FavoriteRestaurantDetails model) {
                Glide.with(requireActivity())
                        .load(model.getRestaurant_image())
                        .placeholder(R.drawable.restaurant_image_placeholder)
                        .into(holder.mFavResImage);

                holder.mFavResName.setText(model.getRestaurant_name());
                holder.mFavResPrice.setText("\u20b9" + model.getRestaurant_price() + " per person");
            }

            @NonNull
            @Override
            public FavoriteResMenuHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.custom_favorite_res_layout, group, false);
                return new FavoriteResMenuHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        mFavoriteResRecyclerView.setAdapter(adapter);

    }

    public static class FavoriteResMenuHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.favResImage)
        ImageView mFavResImage;
        @BindView(R.id.favResName)
        TextView mFavResName;
        @BindView(R.id.favoriteResPrice)
        TextView mFavResPrice;

        public FavoriteResMenuHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
*/
