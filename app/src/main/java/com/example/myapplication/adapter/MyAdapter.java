package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Interfaces.iOnItemClick;
import com.example.myapplication.R;
import com.example.myapplication.model.LocationModel;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    Context context;
    private ArrayList<LocationModel> locationData;
    LayoutInflater inflater;
    private iOnItemClick itemClick;



    public MyAdapter(Context context, ArrayList<LocationModel> locationData) {
        this.context = context;
        this.locationData = locationData;
        this.inflater = LayoutInflater.from(context);
        itemClick = (iOnItemClick) context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.textView.setText(locationData.get(position).getLat());

    }

    @Override
    public int getItemCount() {
        return locationData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lat);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClick.onItemClicked(getAdapterPosition());

        }
    }
}
