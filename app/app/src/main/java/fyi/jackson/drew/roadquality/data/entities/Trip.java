package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class Trip {
    @ColumnInfo(name = "trip_id")
    public long tripId;

    @ColumnInfo(name = "timestamp_start")
    public long startTime;

    @ColumnInfo(name = "timestamp_end")
    public long endTime;

    @ColumnInfo(name = "number_of_points")
    public int numberOfPoints;

    @ColumnInfo(name = "reference_id")
    public String referenceId;

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tripId", tripId);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("numberOfPoints", numberOfPoints);
        jsonObject.put("reference_id", referenceId);
        return jsonObject;
    }

    public String toJSONString() {
        return "{" +
                "'tripId':" + tripId + "," +
                "'startTime':" + startTime + "," +
                "'endTime':" + endTime + "," +
                "'numberOfPoints':" + numberOfPoints + "," +
                "'referenceId':" + referenceId +
                "}";
    }
}
