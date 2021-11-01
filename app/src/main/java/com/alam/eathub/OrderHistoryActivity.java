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

import com.alam.eathub.Adapter.MyOrderAdapterWithPagination;
import com.alam.eathub.Adapter.MyOrderHistoryAdapter;
import com.alam.eathub.Common.Common;

public class OrderHistoryActivity extends AppCompatActivity {

    @BindView(R.id.recycler_order_history)
    RecyclerView recycler_order_history;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    LayoutAnimationController layoutAnimationController;
    MyOrderHistoryAdapter adapter;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_order_history.setLayoutManager(layoutManager);
        recycler_order_history.addItemDecoration(new DividerItemDecoration(this , layoutManager.getOrientation()));

        toolbar.setTitle(getString(R.string.your_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this , R.anim.layout_item_from_left);

        adapter = new MyOrderHistoryAdapter(this , Common.myCurrentUser.getOrders());
        Log.e("ress : " , String.valueOf(Common.myCurrentUser.getOrders().size()));
        recycler_order_history.setAdapter(adapter);
        recycler_order_history.setLayoutAnimation(layoutAnimationController);

    }
}