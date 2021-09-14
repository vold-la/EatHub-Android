package com.alam.eathub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.Addon;
import com.alam.eathub.Model.EventBus.AddOnEventChange;
import com.alam.eathub.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.MyViewHolder> {

    Context context;
    List<Addon> addonList;

    public MyAddonAdapter(Context context, List<Addon> addonList) {
        this.context = context;
        this.addonList = addonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_addon, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.ckb_addon.setText(new StringBuilder(addonList.get(position).getName())
                .append(" +("+context.getString(R.string.money_sign))
        .append(addonList.get(position).getExtraPrice())
        .append(")"));

        holder.ckb_addon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.addonList.add(addonList.get(position));
                    EventBus.getDefault().postSticky(new AddOnEventChange(true , addonList.get(position)));
                }
                else{
                    Common.addonList.remove(addonList.get(position));
                    EventBus.getDefault().postSticky(new AddOnEventChange(false , addonList.get(position)));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ckb_addon)
        CheckBox ckb_addon;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this , itemView);
        }
    }
}
