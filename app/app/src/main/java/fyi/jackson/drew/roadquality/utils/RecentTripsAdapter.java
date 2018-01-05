package fyi.jackson.drew.roadquality.utils;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fyi.jackson.drew.roadquality.R;


public class RecentTripsAdapter extends RecyclerView.Adapter<RecentTripsAdapter.ViewHolder> {
    private static String TAG = "RecentTripsAdapter";
    private JSONArray values;
    private ViewHolder activeViewHolder = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate, textViewTime, textViewPoints;
        private View tripLineTop, tripLineBottom, bottomDividerLine;
        private View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            textViewDate = (TextView) v.findViewById(R.id.tv_date);
            textViewTime = (TextView) v.findViewById(R.id.tv_time);
            textViewPoints = (TextView) v.findViewById(R.id.tv_points);
            tripLineTop = v.findViewById(R.id.view_trip_line_top);
            tripLineBottom = v.findViewById(R.id.view_trip_line_bottom);
            bottomDividerLine = v.findViewById(R.id.bottom_divider_line);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecentTripsAdapter(JSONArray myDataset) {
        values = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.content_bottom_sheet_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tripLineTop.setVisibility(View.VISIBLE);
        holder.tripLineBottom.setVisibility(View.VISIBLE);
        holder.bottomDividerLine.setVisibility(View.VISIBLE);
        if (position == 0) {
            holder.tripLineTop.setVisibility(View.INVISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.tripLineBottom.setVisibility(View.INVISIBLE);
            holder.bottomDividerLine.setVisibility(View.INVISIBLE);
        }
        try {
            long startEpoch = values.getJSONObject(position).getLong("startTime");
            long endEpoch = values.getJSONObject(position).getLong("endTime");
            int points = values.getJSONObject(position).getInt("numberOfPoints");
            holder.textViewDate.setText(
                    helpers.epochToDateString(endEpoch));
            holder.textViewTime.setText(
                    helpers.epochToTimeString(startEpoch) + " - " + helpers.epochToTimeString(endEpoch)
            );
            holder.textViewPoints.setText(points + " points");
        } catch (JSONException e) {
            holder.textViewDate.setText("Parse Error: " + position);
            e.printStackTrace();
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Row clicked: " + position + " - " + holder.textViewTime.getText());
                rowClicked(holder, position);
            }
        });
    }

    public void rowClicked(ViewHolder holder, int position) {
        try {
            JSONObject data = values.getJSONObject(position);
            clearActiveTrips();
            holder.layout.setBackgroundColor(Color.GRAY);
            activeViewHolder = holder;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearActiveTrips() {
        if (activeViewHolder != null) {
            activeViewHolder.layout.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return values.length();
    }


}
