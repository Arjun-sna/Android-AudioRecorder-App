package in.arjsna.audiorecorder.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import in.arjsna.audiorecorder.BuildConfig;
import in.arjsna.audiorecorder.MySharedPreferences;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.activities.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    CheckBoxPreference highQualityPref = (CheckBoxPreference) findPreference(
        getResources().getString(R.string.pref_high_quality_key));
    highQualityPref.setChecked(MySharedPreferences.getPrefHighQuality(getActivity()));
    highQualityPref.setOnPreferenceChangeListener((preference, newValue) -> {
      MySharedPreferences.setPrefHighQuality(getActivity(), (boolean) newValue);
      return true;
    });

    Preference aboutPref = findPreference(getString(R.string.pref_about_key));
    aboutPref.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
    aboutPref.setOnPreferenceClickListener(preference -> {
      LicensesFragment licensesFragment = new LicensesFragment();
      licensesFragment.show(
          ((SettingsActivity) getActivity()).getSupportFragmentManager().beginTransaction(),
          "dialog_licenses");
      return true;
    });
  }
}
