package com.wce.wcevisitcovid19.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wce.wcevisitcovid19.R;
import com.wce.wcevisitcovid19.models.Visitor;
import com.wce.wcevisitcovid19.utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wce.wcevisitcovid19.utils.SearchUtils.zFunction;

public class VisitorsListAdapter extends ArrayAdapter<Visitor> implements Filterable
{
    private final Activity context;
    private static final String TAG = "VisitorsListAdapter";
    ArrayList<Visitor> visitorsList;
    private List<Visitor> tempList;
    List<Visitor> mStringFilterList;
    ValueFilter valueFilter= new ValueFilter();
    int visitorCount;

    public VisitorsListAdapter(Activity context, ArrayList<Visitor> visitorsList){
        super(context, R.layout.visitor_list_item,visitorsList);
        this.context = context;
        this.visitorsList = visitorsList;
        this.visitorCount = visitorsList.size();
        this.mStringFilterList = visitorsList;
        this.tempList = visitorsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.visitor_list_item,null,true);

        TextView nameTextView = rowView.findViewById(R.id.visitor_name_tv);
        TextView placeTextView = rowView.findViewById(R.id.visitor_place_tv);

        String id,name,locationOfVisit;
        try
        {
            name = tempList.get(position).getName();
            locationOfVisit = tempList.get(position).getLocationOfVisit().toLowerCase();
            Log.i(TAG, "getView: getting name: "+name);
        }
        catch (Exception e)
        {
            name = visitorsList.get(position).getName();
            locationOfVisit = visitorsList.get(position).getLocationOfVisit().toLowerCase();
        }

        nameTextView.setText(name);
        placeTextView.setText(locationOfVisit);
        Log.i(TAG, "getView: name & place: "+name+" "+locationOfVisit);
        return rowView;
    }




    @Override
    public Filter getFilter() {
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i(TAG, "performFiltering: Filtering performed automatically..constraint: " + constraint);
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Visitor> filteredValues = new ArrayList<>();
                Log.i(TAG, "performFiltering: mStringFilterList size: " + mStringFilterList.size());
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    String filterString = mStringFilterList.get(i).getName();
                    boolean isPresent = SearchUtils.isPresent(filterString,constraint);
//                    String process = constraint + "@" + filterString;
//                    int[] current = zFunction(process);
//                    boolean isPresent = false;
//                    int targetSize = constraint.length();
//                    for (int j = 0; j < filterString.length(); j++) {
//                        if (current[j + targetSize + 1] == targetSize) {
//                            isPresent = true;
//                            break;
//                        }
//                    }
                    if (isPresent) {
                        filteredValues.add(new Visitor(mStringFilterList.get(i).getId(),filterString,mStringFilterList.get(i).getLocationOfVisit()));
                    }
                }
                results.count = filteredValues.size();
                results.values = filteredValues;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }


            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            tempList = (ArrayList<Visitor>) results.values;
            notifyDataSetChanged();
        }


    }
}
