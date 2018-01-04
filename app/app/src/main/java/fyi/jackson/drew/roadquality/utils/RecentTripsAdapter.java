package fyi.jackson.drew.roadquality.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import fyi.jackson.drew.roadquality.R;


public class RecentTripsAdapter extends RecyclerView.Adapter<RecentTripsAdapter.ViewHolder> {
    private JSONArray values;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewHeader;
        private View tripLineTop, tripLineBottom, bottomDividerLine;
        private View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            textViewHeader = (TextView) v.findViewById(R.id.tv_row_title);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
            holder.textViewHeader.setText(
                    helpers.epochToLocalString(
                            values.getJSONObject(position).getLong("tripId")) +
                            " (" + values.getJSONObject(position).getLong("numberOfPoints") + " points)"
            );
        } catch (JSONException e) {
            holder.textViewHeader.setText("Parse Error: " + position);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return values.length();
    }


}
