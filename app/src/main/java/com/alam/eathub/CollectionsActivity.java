package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.alam.eathub.Adapter.MyAdapter;
import com.alam.eathub.Adapter.MyOrderHistoryAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.EventBus.CollectionsRestaurant;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Model.My.Rrestaurant;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_collection_restaurant)
    RecyclerView mRecyclerView;
    @BindView(R.id.collection_title)
    TextView collectionTitle;
    @BindView(R.id.collection_description)
    TextView collectionDescription;
    @BindView(R.id.collection_total_place)
    TextView collectionTotalPlace;
    @BindView(R.id.collection_img_restaurant)
    KenBurnsView collectionImage;

    LayoutAnimationController layoutAnimationController;
    MyAdapter adapter;

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
        setContentView(R.layout.activity_collections);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadCollectionRestaurants(CollectionsRestaurant event) {

        Picasso.get().load(event.getImageUrl()).into(collectionImage);
        collectionTitle.setText(event.getTitle());
        collectionDescription.setText(event.getDescription());
        String place = event.getNumOfPlaces() + "  places";
        collectionTotalPlace.setText(place);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left);

        adapter = new MyAdapter(this, event.getCollectionRestaurant());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);

    }
}