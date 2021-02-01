package com.wce.wcevisitcovid19.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wce.wcevisitcovid19.R;

import java.util.ArrayList;


public class UserListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<String> userNames=new ArrayList<>();
    ArrayList<String> userTypes = new ArrayList<>();
    private static final String TAG = "UserListAdapter";

    public UserListAdapter(Activity context, ArrayList<String> userNames,ArrayList<String> userTypes){
        super(context, R.layout.simple_user_list_item,userNames);
        this.context = context;
        this.userNames = userNames;
        this.userTypes = userTypes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_user_list_item,null,true);
        TextView userNameTextView=(TextView) rowView.findViewById(R.id.userName_text_view);
        TextView userTypeTextView = rowView.findViewById(R.id.userType_text_view);

        userNameTextView.setText(userNames.get(position));
        userTypeTextView.setText(userTypes.get(position));


        return rowView;
    }
}
