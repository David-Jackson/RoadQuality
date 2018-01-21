package fyi.jackson.drew.roadquality.recycler;

import android.animation.Animator;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fyi.jackson.drew.roadquality.R;
import fyi.jackson.drew.roadquality.animation.listeners.EndAnimatorListener;
import fyi.jackson.drew.roadquality.recycler.holders.NoTripsViewHolder;
import fyi.jackson.drew.roadquality.recycler.holders.ShareViewHolder;
import fyi.jackson.drew.roadquality.recycler.holders.TripViewHolder;
import fyi.jackson.drew.roadquality.utils.OnClickListenerWithCoordinates;
import fyi.jackson.drew.roadquality.utils.helpers;


public abstract class RecentTripsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "RecentTripsAdapter";
    private final ArrayList<Object> values;
    private TripViewHolder activeTripViewHolder = null;
    private int activeTripPosition = -1;
    private ViewGroup viewGroup;

    private static final int TRIP = 0, SHARE = 1, NO_TRIPS = 2;

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
        viewGroup = parent;
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
        holder.backgroundSelected.setVisibility(View.INVISIBLE);
        if (position == 0) {
            holder.tripLineTop.setVisibility(View.INVISIBLE);
        }
        if (position == getItemCount() - 2) {
            holder.tripLineBottom.setVisibility(View.INVISIBLE);
            holder.bottomDividerLine.setVisibility(View.INVISIBLE);
        }
        if (position == activeTripPosition) {
            holder.backgroundSelected.setVisibility(View.VISIBLE);
        }
        try {
            final JSONObject obj = (JSONObject) values.get(position);
            final long tripId = obj.getLong("tripId");
            long startEpoch = obj.getLong("startTime");
            long endEpoch = obj.getLong("endTime");
            int points = obj.getInt("numberOfPoints");
            Log.d(TAG, "onBindTripViewHolder: referenceId: " + obj.getString("referenceId"));
            boolean uploaded = !obj.getString("referenceId").equals("null");

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

            String btnText = (uploaded ?
                    res.getString(R.string.uploaded) :
                    res.getString(R.string.upload));
            holder.uploadButton.setText(btnText);
            holder.uploadButton.setEnabled(!uploaded);

            holder.uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUploadButtonClicked(tripId);
                }
            });
        } catch (JSONException e) {
            Resources res = holder.layout.getResources();
            String parseErrorString = String.format(res.getString(R.string.points_parse_error), position);
            holder.textViewDate.setText(parseErrorString);
            e.printStackTrace();
        }

        final boolean isExpanded = position == activeTripPosition;
        holder.uploadButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.layout.setActivated(isExpanded);

        holder.layout.setOnTouchListener(new OnClickListenerWithCoordinates() {
            @Override
            public void onClick(float clickX, float clickY) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                }
                rowClicked(holder, position, clickX, clickY);
                notifyDataSetChanged();
            }
        });
    }

    public void rowClicked(TripViewHolder holder, int position, float clickX, float clickY) {
        try {
            JSONObject data = (JSONObject) values.get(position);
            if (activeTripViewHolder != null && position == activeTripViewHolder.getAdapterPosition()) {
                if (onRowClickedAgain(data.getLong("tripId"))) {
                    clearActiveTrips(clickX, clickY);
                    activeTripPosition = -1;
                }
            } else {
                clearActiveTrips(-1, -1);
                holder.backgroundSelected.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Get the distance from the click location to the bottom or top of the view,
                    // whichever is largest
                    float cx = holder.layout.getWidth() +
                            holder.layout.getX() -
                            (-clickX + (holder.layout.getWidth() / 2));
                    // Get the distance from the click location to the left or right of the view,
                    // whichever is largest
                    float cy = holder.layout.getHeight() +
                            holder.layout.getY() -
                            (-clickY + (holder.layout.getHeight() / 2));
                    ViewAnimationUtils.createCircularReveal(holder.backgroundSelected,
                            (int) clickX,
                            (int) clickY,
                            0,
                            (float) Math.hypot(cx, cy))
                            .start();
                }
                onRowClicked(data.getLong("tripId"));
                activeTripViewHolder = holder;
                activeTripPosition = position;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onRowClicked(long tripId);

    public abstract boolean onRowClickedAgain(long tripId);

    public abstract void onUploadButtonClicked(long tripId);

    public abstract void onShareButtonClick();

    public abstract void onSettingsButtonClick();

    public void clearActiveTrips(float clickX, float clickY) {
        if (activeTripViewHolder != null) {
            if ((clickX == -1 && clickY == -1) || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                activeTripViewHolder.backgroundSelected.setVisibility(View.INVISIBLE);
                activeTripViewHolder = null;
                activeTripPosition = -1;
            } else {
                // Get the distance from the click location to the bottom or top of the view,
                // whichever is largest
                float cx = activeTripViewHolder.layout.getWidth() +
                        activeTripViewHolder.layout.getX() -
                        (-clickX + (activeTripViewHolder.layout.getWidth() / 2));
                // Get the distance from the click location to the left or right of the view,
                // whichever is largest
                float cy = activeTripViewHolder.layout.getHeight() +
                        activeTripViewHolder.layout.getY() -
                        (-clickY + (activeTripViewHolder.layout.getHeight() / 2));

                Animator animator = ViewAnimationUtils
                        .createCircularReveal(activeTripViewHolder.backgroundSelected,
                                (int) clickX,
                                (int) clickY,
                                (float) Math.hypot(cx, cy),
                                0);
                animator.addListener(new EndAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        activeTripViewHolder.backgroundSelected.setVisibility(View.INVISIBLE);
                        activeTripViewHolder = null;
                        activeTripPosition = -1;
                    }
                });
                animator.start();
            }
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


}
