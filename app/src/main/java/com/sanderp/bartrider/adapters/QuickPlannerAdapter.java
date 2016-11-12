package com.sanderp.bartrider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.structure.Trip;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sander Peerna on 11/3/2016.
 */

public class QuickPlannerAdapter extends BaseAdapter {
    private static final String TAG = "QuickPlannerAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Trip> mDataSource;

    public QuickPlannerAdapter(Context context, List<Trip> trips) {
        mContext = context;
        mDataSource = trips;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item, parent, false);

        // Initialize text fields
        TextView mOrigTime = (TextView) rowView.findViewById(R.id.originTime);
        TextView mDestTime = (TextView) rowView.findViewById(R.id.destinationTime);
        TextView mFare = (TextView) rowView.findViewById(R.id.fare);

        Trip d = (Trip) getItem(position);
        mOrigTime.setText(d.getOrigTimeMin());
        mDestTime.setText(d.getDestTimeMin());
        mFare.setText("$" + new DecimalFormat("#.00").format(d.getFare()));

        return rowView;
    }
}
