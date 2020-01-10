package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.LocationModel;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    Context context;
    ArrayList<LocationModel> locationdata;
    LayoutInflater inflater;
    MyInterface myInterface;


    public interface MyInterface {
        void onItemClicked(int i);

    }

    public MyAdapter(Context context, ArrayList<LocationModel> locationdata) {
        this.context = context;
        this.locationdata = locationdata;
        this.inflater = LayoutInflater.from(context);
        myInterface = (MyInterface) context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.textView.setText(locationdata.get(position).getLat());

    }

    @Override
    public int getItemCount() {
        return locationdata.size();
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
                myInterface.onItemClicked(getAdapterPosition());

        }
    }
}
