package com.wce.wcevisitcovid19.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wce.wcevisitcovid19.R;

import java.util.ArrayList;

public class SymptomsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> symptoms=new ArrayList<>();
    private static final String TAG = "SymptomsListAdapter";

    public SymptomsListAdapter(Activity context, ArrayList<String> symptoms){
        super(context, R.layout.simple_list_item,symptoms);
        this.context = context;
        this.symptoms = symptoms;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView symptomTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        symptomTextView.setText(symptoms.get(position));
        Log.i(TAG, "getView: Symptom: "+symptoms.get(position));
        return rowView;
    }
}
