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

public class SimpleListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<String> users=new ArrayList<>();
    private static final String TAG = "SimpleListAdapter";
    public SimpleListAdapter(Activity context, ArrayList<String> users){
        super(context, R.layout.simple_list_item,users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView studentTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        studentTextView.setText(users.get(position));
        Log.i(TAG, "getView: User: "+users.get(position));
        return rowView;
    }
}
