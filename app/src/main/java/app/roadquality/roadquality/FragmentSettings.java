package app.roadquality.roadquality;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class FragmentSettings extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
    }
}
