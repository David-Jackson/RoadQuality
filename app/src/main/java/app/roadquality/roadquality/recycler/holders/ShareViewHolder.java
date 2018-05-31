package app.roadquality.roadquality.recycler.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import app.roadquality.roadquality.R;

public class ShareViewHolder extends RecyclerView.ViewHolder {
    public final View layout;
    public final ImageButton shareButton, settingsButton;
    public ShareViewHolder(View v) {
        super(v);
        layout = v;
        shareButton = v.findViewById(R.id.share_button);
        settingsButton = v.findViewById(R.id.settings_button);
    }
}
