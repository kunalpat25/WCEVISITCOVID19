package com.wce.wcevisitcovid19.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wce.wcevisitcovid19.R;

import java.util.ArrayList;

public class VisitorsListAdapter extends ArrayAdapter<String>
{
    private final Activity context;
    private static final String TAG = "VisitorsListAdapter";
    ArrayList<String> outsiderNames=new ArrayList<>();
    ArrayList<String> outsiderAddresses = new ArrayList<>();

    public VisitorsListAdapter(Activity context, ArrayList<String> outsiderNames,ArrayList<String> outsiderAddresses){
        super(context, R.layout.visitor_list_item,outsiderNames);
        this.context = context;
        this.outsiderNames = outsiderNames;
        this.outsiderAddresses = outsiderAddresses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.visitor_list_item,null,true);

        TextView nameTextView = rowView.findViewById(R.id.visitor_name_tv);
        TextView placeTextView = rowView.findViewById(R.id.visitor_place_tv);

        nameTextView.setText(outsiderNames.get(position));
        placeTextView.setText(outsiderAddresses.get(position));
        return rowView;
    }
}
