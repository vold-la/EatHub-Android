package com.alam.eathub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.FoodDetailActivity;
import com.alam.eathub.Interface.IOnRecyclerViewClickListener;
import com.alam.eathub.Model.EventBus.FoodDetailEvent;
import com.alam.eathub.Model.Favorite;
import com.alam.eathub.Model.My.CuisineCheckbox;
import com.alam.eathub.Model.My.CuisineCheckboxModel;
import com.alam.eathub.Model.RestaurantModel;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyCuisineCheckboxAdapter extends RecyclerView.Adapter<MyCuisineCheckboxAdapter.MyViewHolder> {

    Context context;
    List<CuisineCheckboxModel> list;
    private List<Integer> checkedIndex;
    private List<String> checkedName;
    private String name[] = {
            "Afghan" , "American" , "Andhra" , "Arabian" , "Asian" , "Assamese", "Awadhi", "BBQ"
            , "Bakery" , "Bangladeshi" ,  "Belgian" ,"Bengali" , "Bihari" ,"Biryani" , "British" ,
            "Charcoal Chicken" , "Chinese" , "Coffee", "Continental" , "Desserts" ,  "European" ,
            "Finger Food" , "French" , "Gujarati" , "Healthy Food" , "Hot dogs" , "Hyderabadi" , "Ice Cream",
            "Indonesian" , "Iranian" , "Italian" ,  "Japanese" , "Juices" , "Kashmiri" , "Kebab" , "Kerala" ,
            "Korean" , "Lebanese" ,"Lucknowi" , "Maharashtrian", "Malaysian" , "Mediterranean" , "Mexican",
            "Mishti","Mithai","Momos","Mughlai","North Indian","Paan" ,"Pasta","Pizza","Rajasthani","Roast Chicken"
            ,"Rolls","Russian","Salad","Sandwich","Seafood","Singaporean","South Indian" ,"Spanish" ,"Sri Lankan",
            "Steak" ,"Sushi" ,"Tamil","Tea","Thai", "Tibetan","Turkish","Wraps"};
    private boolean isSelected[] = new boolean[70];

    public MyCuisineCheckboxAdapter(Context context, List<Integer> b) {
        this.context = context;
        if(b != null) {
            Log.e("res b:" , String.valueOf(b));
            checkedIndex = new ArrayList<>();
            checkedName = new ArrayList<>();
            for (int i = 0; i < b.size(); i++) {
                isSelected[b.get(i)] = true;
                checkedIndex.add(b.get(i));
                checkedName.add(name[b.get(i)]);
            }
        }
    }

    @NonNull
    @Override
    public MyCuisineCheckboxAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCuisineCheckboxAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.cuisine_checkbox, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCuisineCheckboxAdapter.MyViewHolder holder, int position) {

        holder.cuisineText.setText(name[position]);
        if(isSelected[position])
            holder.cuisineCheck.setChecked(true);
        else
            holder.cuisineCheck.setChecked(false);

        holder.cuisineCheck.setOnClickListener(view -> {
            if(isSelected[position]){
                checkedIndex.remove(Integer.valueOf(position));
                checkedName.remove(name[position]);
                Log.e("res uncheck:" , String.valueOf(checkedName));
                Log.e("res uncheck:" , String.valueOf(checkedIndex));
                isSelected[position] = false;
                holder.cuisineCheck.setChecked(false);
            }
            else{
                if(checkedIndex == null) {
                    checkedIndex = new ArrayList<>();
                    checkedName = new ArrayList<>();
                }
                    checkedIndex.add(position);
                    checkedName.add(name[position]);
                Log.e("res check:" , String.valueOf(checkedName));
                Log.e("res check:" , String.valueOf(checkedIndex));
                    isSelected[position] = true;
                    holder.cuisineCheck.setChecked(true);
            }
        });

        //Event
       // holder.setListener((view, position1) -> {
         //           });
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public List<Integer> getCheckedIndex(){
        Log.e("res get:" , String.valueOf(checkedIndex));
        return checkedIndex;
    }
    public List<String> getCheckedName(){
        Log.e("res get:" , String.valueOf(checkedName));
        return checkedName;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cuisineText)
        TextView cuisineText;
        @BindView(R.id.cuisineCheck)
        CheckBox cuisineCheck;

        Unbinder unbinder;
        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
