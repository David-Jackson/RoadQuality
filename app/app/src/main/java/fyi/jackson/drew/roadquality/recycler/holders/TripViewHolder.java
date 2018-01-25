package fyi.jackson.drew.roadquality.recycler.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fyi.jackson.drew.roadquality.R;

public class TripViewHolder extends RecyclerView.ViewHolder {
    public final TextView textViewDate, textViewTime, textViewPoints;
    public final View tripLineTop, tripLineBottom, bottomDividerLine;
    public final Button uploadButton;
    public final View layout;

    public TripViewHolder(View v) {
        super(v);
        layout = v;
        textViewDate = v.findViewById(R.id.tv_date);
        textViewTime = v.findViewById(R.id.tv_time);
        textViewPoints = v.findViewById(R.id.tv_points);
        tripLineTop = v.findViewById(R.id.view_trip_line_top);
        tripLineBottom = v.findViewById(R.id.view_trip_line_bottom);
        bottomDividerLine = v.findViewById(R.id.bottom_divider_line);
        uploadButton = v.findViewById(R.id.button_upload);
    }
}