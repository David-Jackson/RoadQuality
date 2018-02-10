package fyi.jackson.drew.roadquality.recycler;

import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fyi.jackson.drew.roadquality.R;
import fyi.jackson.drew.roadquality.data.entities.Trip;
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
    private ViewGroup viewGroup = null;

    private static final int TRIP = 0, SHARE = 1, NO_TRIPS = 2;

    // Provide a suitable constructor (depends on the kind of data set)
    public RecentTripsAdapter(List<Trip> tripList) {
        //setHasStableIds(true);
        values = new ArrayList<>();
        if (tripList.size() == 0) {
            values.add(NO_TRIPS);
        }

        for (Trip trip : tripList) {
            values.add(trip);
        }

        values.add(SHARE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewGroup == null) viewGroup = parent;
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
        if (values.get(position) instanceof Trip) {
            return TRIP;
        } else {
            return (int) values.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == TRIP) {
            return ((Trip) values.get(position)).tripId;
        } else {
            return super.getItemId(position);
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
        boolean isFistTrip = position == 0;
        boolean isLastTrip = position == getItemCount() - 2;

        holder.tripLineTop.setVisibility(isFistTrip ? View.INVISIBLE : View.VISIBLE);
        holder.tripLineBottom.setVisibility(isLastTrip ? View.INVISIBLE : View.VISIBLE);
        holder.bottomDividerLine.setVisibility(isLastTrip ? View.INVISIBLE : View.VISIBLE);

        final Trip trip = (Trip) values.get(position);
        boolean uploaded = !(trip.referenceId == null);

        Resources res = holder.layout.getResources();
        String dateString = helpers.epochToDateString(trip.endTime);
        String durationString = String.format(
                res.getString(R.string.duration_string),
                helpers.epochToTimeString(trip.startTime),
                helpers.epochToTimeString(trip.endTime)
        );
        String numberOfPoints = res.getQuantityString(
                R.plurals.number_of_points, trip.numberOfPoints, trip.numberOfPoints);

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
                onUploadButtonClicked(trip.tripId);
            }
        });

        final boolean isExpanded = position == activeTripPosition;
        holder.uploadButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.layout.setActivated(isExpanded);

        holder.layout.setOnTouchListener(new OnClickListenerWithCoordinates() {
            @Override
            public void onClick(float clickX, float clickY) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                }
                int oldActiveTripPosition = activeTripPosition;
                rowClicked(holder, position, clickX, clickY);
                int minPosition = Math.min(activeTripPosition, oldActiveTripPosition);
                notifyItemRangeChanged(minPosition, values.size() - minPosition);
            }
        });
    }

    public void rowClicked(TripViewHolder holder, int position, float clickX, float clickY) {
        Trip trip = (Trip) values.get(position);
        if (activeTripViewHolder != null && position == activeTripPosition) {
            if (onRowClickedAgain(trip.tripId)) {
                activeTripViewHolder = holder;
                activeTripPosition = -1;
            }
        } else {
            onRowClicked(trip.tripId);
            activeTripViewHolder = holder;
            activeTripPosition = position;
        }
    }

    public abstract void onRowClicked(long tripId);

    public abstract boolean onRowClickedAgain(long tripId);

    public abstract void onUploadButtonClicked(long tripId);

    public abstract void onShareButtonClick();

    public abstract void onSettingsButtonClick();

    @Override
    public int getItemCount() {
        return values.size();
    }


}
