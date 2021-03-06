package com.wce.wcevisitcovid19.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
public class SeniorCitizensListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> seniorCitizens=new ArrayList<>();
    private static final String TAG = "SeniorCitizensListAdapt";

    public SeniorCitizensListAdapter(Activity context, ArrayList<String> seniorCitizens){
        super(context, R.layout.simple_list_item,seniorCitizens);
        this.context = context;
        this.seniorCitizens = seniorCitizens;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView symptomTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        symptomTextView.setText(seniorCitizens.get(position));
        Log.i(TAG, "getView: Citizen: "+seniorCitizens.get(position));
        return rowView;
    }
}
