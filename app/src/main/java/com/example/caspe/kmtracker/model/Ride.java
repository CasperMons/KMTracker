package com.example.caspe.kmtracker.model;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;

/**
 * Created by caspe on 26-3-2018.
 */

public class Ride implements Comparable<Ride>{
    public static DecimalFormat priceFormat = new DecimalFormat("##0.00");
    public static final String UNREGISTERED = "N/A";
    public static final double RIDE_PRICE = 0.15;

    public String username;
    public long distance;
    public long date;

    @Override
    public int compareTo(@NonNull Ride another) {
        long compareValue = another.date;
        return (this.date < compareValue ? -1 : (compareValue == this.date ? 0 : 1));
    }
}
