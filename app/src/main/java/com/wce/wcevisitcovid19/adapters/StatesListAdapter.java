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

public class StatesListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<String> states = new ArrayList<>();
    private static final String TAG = "StatesListAdapter";

    public StatesListAdapter(Activity context, ArrayList<String> states)
    {
        super(context, R.layout.simple_list_item,states);
        this.context = context;
        this.states = states;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView stateTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        stateTextView.setText(states.get(position));
        Log.i(TAG, "getView: State: "+states.get(position));
        return rowView;
    }
}
