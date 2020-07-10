package com.wce.wcevisitcovid19;

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

import java.util.ArrayList;

public class SymptomaticStudentListAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<String> students=new ArrayList<>();
    private static final String TAG = "SymptomsListAdapter";

    public SymptomaticStudentListAdapter(Activity context, ArrayList<String> students){
        super(context,R.layout.simple_list_item,students);
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.simple_list_item,null,true);
        TextView studentTextView=(TextView) rowView.findViewById(R.id.list_item_text_view);

        studentTextView.setText(students.get(position));
        Log.i(TAG, "getView: Student: "+students.get(position));
        return rowView;
    }
}
