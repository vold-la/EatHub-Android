package com.alam.eathub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.FoodListActivity;
import com.alam.eathub.Interface.IOnRecyclerViewClickListener;
import com.alam.eathub.Model.Category;
import com.alam.eathub.Model.EventBus.FoodListEvent;
import com.alam.eathub.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryList;

    public MyCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        Picasso.get().load(Uri.parse("android.resource://"+context.getPackageName()+"/drawable/"+ categoryList.get(position).getImage())).into(holder.img_category);
        holder.txt_category.setText(categoryList.get(position).getName());

        holder.setListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                //postSticky -> event can be listen from other activity unlike post
                // send sticky post event to FoodListActivity
                EventBus.getDefault().postSticky(new FoodListEvent(true,categoryList.get(position)));
                context.startActivity(new Intent(context , FoodListActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_category)
        ImageView img_category;
        @BindView(R.id.txt_category)
        TextView txt_category;
        Unbinder unbinder;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(categoryList.size() == 1)
            return Common.DEFAULT_COLUMN_TYPE;
        else {
            if(categoryList.size() %2 == 0)
                return Common.DEFAULT_COLUMN_TYPE;
            else
                return (position >1 && position == categoryList.size()-1) ?Common.FULL_WIDTH_COLUMN:Common.DEFAULT_COLUMN_TYPE;
        }
    }
}
