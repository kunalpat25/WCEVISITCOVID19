package com.wce.wcevisitcovid19.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wce.wcevisitcovid19.R;

import java.util.ArrayList;

public class DistrictsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> districts=new ArrayList<>();
    private static final String TAG = "DistrictsListAdapter";

    public DistrictsListAdapter(Activity context, ArrayList<String> districts){
        super(context, R.layout.simple_list_item,districts);
        this.context = context;
        this.districts = districts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView districtTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        districtTextView.setText(districts.get(position));
        return rowView;
    }
}
