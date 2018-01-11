package fyi.jackson.drew.roadquality.utils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fyi.jackson.drew.roadquality.R;


public abstract class RecentTripsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "RecentTripsAdapter";
    private final ArrayList<Object> values;
    private TripViewHolder activeTripViewHolder = null;

    private static final int TRIP = 0, SHARE = 1, NO_TRIPS = 2;

    public class TripViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate, textViewTime, textViewPoints;
        private final View tripLineTop, tripLineBottom, bottomDividerLine;
        private final View layout;

        public TripViewHolder(View v) {
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

    public class ShareViewHolder extends RecyclerView.ViewHolder {
        private final View layout;
        private final ImageButton shareButton, settingsButton;
        public ShareViewHolder(View v) {
            super(v);
            layout = v;
            shareButton = v.findViewById(R.id.share_button);
            settingsButton = v.findViewById(R.id.settings_button);
        }
    }

    public class NoTripsViewHolder extends RecyclerView.ViewHolder {
        public NoTripsViewHolder(View v) {
            super(v);
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public RecentTripsAdapter(JSONArray myDataSet) {
        values = new ArrayList<>();
        if (myDataSet.length() == 0) {
            values.add(NO_TRIPS);
        }

        try {
            for (int i = 0; i < myDataSet.length(); i++) {
                values.add(myDataSet.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        values.add(SHARE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case SHARE:
                View v1 = inflater.inflate(R.layout.content_bottom_sheet_last_row, parent, false);
                viewHolder = new ShareViewHolder(v1);
                break;
            case NO_TRIPS:
                View v2 = inflater.inflate(R.layout.content_bottom_sheet_no_trips_row, parent, false);
                viewHolder = new NoTripsViewHolder(v2);
                break;
            default: // TRIP
                View v = inflater.inflate(R.layout.content_bottom_sheet_row, parent, false);
                viewHolder = new TripViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (values.get(position) instanceof JSONObject) {
            return TRIP;
        } else {
            return (int) values.get(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SHARE:
                onBindShareViewHolder((ShareViewHolder) holder, position);
                break;
            case NO_TRIPS:
                onBindNoTripViewHolder((NoTripsViewHolder) holder, position);
                break;
            default: // TRIP
                onBindTripViewHolder((TripViewHolder) holder, position);
                break;
        }
    }

    private void onBindNoTripViewHolder(NoTripsViewHolder holder, int position) {

    }

    private void onBindShareViewHolder(ShareViewHolder holder, int position) {
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareButtonClick();
            }
        });
        holder.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsButtonClick();
            }
        });
    }

    private void onBindTripViewHolder(final TripViewHolder holder, final int position) {
        holder.tripLineTop.setVisibility(View.VISIBLE);
        holder.tripLineBottom.setVisibility(View.VISIBLE);
        holder.bottomDividerLine.setVisibility(View.VISIBLE);
        if (position == 0) {
            holder.tripLineTop.setVisibility(View.INVISIBLE);
        }
        if (position == getItemCount() - 2) {
            holder.tripLineBottom.setVisibility(View.INVISIBLE);
            holder.bottomDividerLine.setVisibility(View.INVISIBLE);
        }
        try {
            JSONObject obj = (JSONObject) values.get(position);
            long startEpoch = obj.getLong("startTime");
            long endEpoch = obj.getLong("endTime");
            int points = obj.getInt("numberOfPoints");

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

    public void rowClicked(TripViewHolder holder, int position) {
        try {
            JSONObject data = (JSONObject) values.get(position);
            if (activeTripViewHolder != null && position == activeTripViewHolder.getAdapterPosition()) {
                if (onRowClickedAgain(data.getLong("tripId"))) {
                    clearActiveTrips();
                }
            } else {
                clearActiveTrips();
                holder.layout.setBackgroundColor(Color.GRAY);
                onRowClicked(data.getLong("tripId"));
                activeTripViewHolder = holder;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onRowClicked(long tripId);

    public abstract boolean onRowClickedAgain(long tripId);

    public abstract void onShareButtonClick();

    public abstract void onSettingsButtonClick();

    public void clearActiveTrips() {
        if (activeTripViewHolder != null) {
            activeTripViewHolder.layout.setBackgroundColor(
                    activeTripViewHolder.layout.getResources().getColor(R.color.bottom_sheet_background));
            activeTripViewHolder = null;
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


}
