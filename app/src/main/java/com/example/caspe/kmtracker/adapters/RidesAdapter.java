package com.example.caspe.kmtracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caspe.kmtracker.R;
import com.example.caspe.kmtracker.model.Ride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by caspe on 26-3-2018.
 */

public class RidesAdapter extends ArrayAdapter<Ride> {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");

    public RidesAdapter(Context context, ArrayList<Ride> rides) {
        super(context, 0, rides);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ride ride = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_rides, parent, false);
        }

        TextView txtDate = (TextView) convertView.findViewById(R.id.txt_date);
        TextView txtDistance = (TextView) convertView.findViewById(R.id.txt_distance);
        TextView txtUser = (TextView) convertView.findViewById(R.id.txt_username);
        TextView txtPrice = (TextView) convertView.findViewById(R.id.txt_price);
        if (ride != null) {
            double price = ride.distance * Ride.RIDE_PRICE;
            txtDate.setText(dateFormat.format(new Date(ride.date)));
            txtDistance.setText(Long.toString(ride.distance));
            txtPrice.setText("€" + Ride.priceFormat.format(price));

            if (ride.username == null || ride.username.equals("")){
                txtUser.setText(Ride.UNREGISTERED);
                txtUser.setTextColor(convertView.getResources().getColor(R.color.txt_unregistered, null));
            }else{
                txtUser.setText(ride.username);
                txtUser.setTextColor(convertView.getResources().getColor(R.color.txt_registered, null));
            }
        }
        return convertView;
    }
}
