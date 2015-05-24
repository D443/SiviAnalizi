package com.hamdyghanem.httprequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Created by ICHIMARU on 08.05.2015.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    Double[] avgHeat,avgPh;
    String[] times;
    public CustomAdapter(Context context, String[] times, Double[] Heat, Double[] Ph) {
        super(context, R.layout.dailyrow, times);
        this.times = times;
        avgHeat = Heat;
        avgPh = Ph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        View customvView = mInflater.inflate(R.layout.dailyrow,parent,false);
        //x values
        Double xHeat,xPh;
        String xTimes;
        //set x values
        xTimes = this.times[position];
        xHeat = this.avgHeat[position];
        xPh = this.avgPh[position];
        //Define items
        TextView tvTime = (TextView)customvView.findViewById(R.id.tvTime);
        TextView tvHeat = (TextView)customvView.findViewById(R.id.tvHeat);
        TextView tvPh = (TextView)customvView.findViewById(R.id.tvPh);
        ImageView ivShape = (ImageView)customvView.findViewById(R.id.imageView);
        //set items
        DecimalFormat df = new DecimalFormat("#.##");
        tvTime.setText(xTimes);
        tvHeat.setText("H: " + df.format(xHeat));
        tvPh.setText("Ph: " + df.format(xPh));
        ivShape.setImageResource(R.drawable.bracket);

        return customvView;
    }
}
