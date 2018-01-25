package fyi.jackson.drew.roadquality;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import fyi.jackson.drew.roadquality.animation.MorphingFab;
import fyi.jackson.drew.roadquality.service.ServiceConstants;
import fyi.jackson.drew.roadquality.utils.BroadcastManager;

public class ActivityTest extends AppCompatActivity {

    private String TAG = "ActivityTest";
    private MorphingFab morphingFab;

    private BroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        morphingFab = new MorphingFab(this,
//                (FloatingActionButton) findViewById(R.id.fab), // FAB
//                findViewById(R.id.fab_reveal),                 // View after FAB Clicked
//                R.id.fab_reveal_image,                         // Shared Element
//                R.drawable.avd_play_to_pause_96dp,             // Animated Vector Drawable (AVD) closed to open
//                R.drawable.avd_pause_to_play_96dp) {           // AVD open to close
//            @Override
//            public boolean onFabClick() {
//                return true;
//            }
//        };

        broadcastManager = new BroadcastManager(this) {
            @Override
            public void onServiceStatusChanged(int serviceStatus) {

            }

            @Override
            public void onDataTransferredToLongTerm(int totalRows, int deletedAccelRows, int deletedGpsRows) {

            }

            @Override
            public void onTripUploadReceived(int status, String referenceId) {
                String statusStr = (status == ServiceConstants.UPLOAD_TRIP_SUCCESS ? "Success" : "Failure") + ": ";
                statusStr += (referenceId == null ? "null" : referenceId);
                Toast.makeText(getApplicationContext(), statusStr, Toast.LENGTH_SHORT).show();
            }
        };

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastManager.askToUploadTrip(1515984165333L);
            }
        });

    }

    @Override
    protected void onResume() {
        if (broadcastManager != null) broadcastManager.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (broadcastManager != null) broadcastManager.onPause();
        super.onPause();
    }
}
