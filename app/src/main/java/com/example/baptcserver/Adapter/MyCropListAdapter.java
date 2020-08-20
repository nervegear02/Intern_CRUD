package com.example.baptcserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baptcserver.Callback.IRecyclerClickListener;
import com.example.baptcserver.Common.Common;
import com.example.baptcserver.EventBus.CropItemClick;
import com.example.baptcserver.Model.CropModel;
import com.example.baptcserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCropListAdapter extends RecyclerView.Adapter<MyCropListAdapter.MyViewHolder> {
    private Context context;
    private List<CropModel> cropModelList;

    public MyCropListAdapter(Context context, List<CropModel> cropModelList) {
        this.context = context;
        this.cropModelList = cropModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_crop_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cropModelList.get(position).getImage()).into(holder.img_crop_image);
        holder.txt_crop_price.setText(new StringBuilder("₱")
                .append(cropModelList.get(position).getPrice()));
        holder.txt_crop_name.setText(new StringBuilder("")
                .append(cropModelList.get(position).getName()));

        //Event
        holder.setListener((view, pos) -> {
            Common.selectedCrop = cropModelList.get(pos);

        });

    }

    @Override
    public int getItemCount() {
        return cropModelList.size();
    }

    public CropModel getItemAtPosition(int pos) {
        return cropModelList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_crop_name)
        TextView txt_crop_name;
        @BindView(R.id.txt_crop_price)
        TextView txt_crop_price;
        @BindView(R.id.img_crop_image)
        ImageView img_crop_image;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
