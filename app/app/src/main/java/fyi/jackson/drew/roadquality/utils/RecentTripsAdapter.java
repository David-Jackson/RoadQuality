package fyi.jackson.drew.roadquality.utils;

import android.content.res.Resources;
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


public abstract class RecentTripsAdapter extends RecyclerView.Adapter<RecentTripsAdapter.ViewHolder> {
    private final static String TAG = "RecentTripsAdapter";
    private final JSONArray values;
    private ViewHolder activeViewHolder = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate, textViewTime, textViewPoints;
        private final View tripLineTop, tripLineBottom, bottomDividerLine;
        private final View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            textViewDate = v.findViewById(R.id.tv_date);
            textViewTime = v.findViewById(R.id.tv_time);
            textViewPoints = v.findViewById(R.id.tv_points);
            tripLineTop = v.findViewById(R.id.view_trip_line_top);
            tripLineBottom = v.findViewById(R.id.view_trip_line_bottom);
            bottomDividerLine = v.findViewById(R.id.bottom_divider_line);
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public RecentTripsAdapter(JSONArray myDataSet) {
        values = myDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.content_bottom_sheet_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
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

            Resources res = holder.layout.getResources();
            String dateString = helpers.epochToDateString(endEpoch);
            String durationString = String.format(
                    res.getString(R.string.duration_string),
                    helpers.epochToTimeString(startEpoch),
                    helpers.epochToTimeString(endEpoch)
            );
            String numberOfPoints = res.getQuantityString(R.plurals.number_of_points, points, points);

            holder.textViewDate.setText(dateString);
            holder.textViewTime.setText(durationString);
            holder.textViewPoints.setText(numberOfPoints);
        } catch (JSONException e) {
            Resources res = holder.layout.getResources();
            String parseErrorString = String.format(res.getString(R.string.points_parse_error), position);
            holder.textViewDate.setText(parseErrorString);
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
            if (activeViewHolder != null && position == activeViewHolder.getAdapterPosition()) {
                if (onRowClickedAgain(data.getLong("tripId"))) {
                    clearActiveTrips();
                }
            } else {
                clearActiveTrips();
                holder.layout.setBackgroundColor(Color.GRAY);
                onRowClicked(data.getLong("tripId"));
                activeViewHolder = holder;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onRowClicked(long tripId);

    public abstract boolean onRowClickedAgain(long tripId);

    public void clearActiveTrips() {
        if (activeViewHolder != null) {
            activeViewHolder.layout.setBackgroundColor(
                    activeViewHolder.layout.getResources().getColor(R.color.bottom_sheet_background));
            activeViewHolder = null;
        }
    }

    @Override
    public int getItemCount() {
        return values.length();
    }


}
