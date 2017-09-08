package in.arjsna.audiorecorder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.SettingBasic;
import in.arjsna.audiorecorder.theme.ColorsSetting;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import in.arjsna.audiorecorder.theme.ThemedActivity;
import in.arjsna.audiorecorder.theme.ThemedFragment;

public class SettingsFragment extends ThemedFragment {

  private View rootView;
  private SettingBasic themeSetting;
  private ThemedActivity parent;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_settings, container, false);
    parent = (ThemedActivity) getActivity();
    initViews();
    bindEvents();
    return rootView;
  }

  private void bindEvents() {
    themeSetting.setOnClickListener(v -> {
      final int originalColor = getPrimaryColor();
      new ColorsSetting((ThemedActivity) getActivity()).chooseColor(R.string.primary_color,
          new ColorsSetting.ColorChooser() {
            @Override
            public void onColorSelected(int color) {
              Hawk.put(getString(R.string.preference_primary_color), color);
              parent.updateTheme();
              parent.updateUiElements();
            }

            @Override
            public void onDialogDismiss() {
              Hawk.put(getString(R.string.preference_primary_color), originalColor);
              parent.updateTheme();
              parent.updateUiElements();
            }

            @Override
            public void onColorChanged(int color) {
              Hawk.put(getString(R.string.preference_primary_color), color);
              parent.updateTheme();
              parent.updateUiElements();
            }
          }, getPrimaryColor());
    });
  }

  private void initViews() {
    themeSetting = (SettingBasic) rootView.findViewById(R.id.theme_settings);
  }

  //Preference aboutPref = findPreference(getString(R.string.pref_about_key));
  //  aboutPref.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
  //  aboutPref.setOnPreferenceClickListener(preference -> {
  //  LicensesFragment licensesFragment = new LicensesFragment();
  //  licensesFragment.show(
  //      ((SettingsActivity) getActivity()).getSupportFragmentManager(),
  //      "dialog_licenses");
  //  return true;
  //});

  @Override public void refreshTheme(ThemeHelper themeHelper) {

  }
}
