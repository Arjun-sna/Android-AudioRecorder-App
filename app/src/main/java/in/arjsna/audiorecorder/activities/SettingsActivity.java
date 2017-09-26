package in.arjsna.audiorecorder.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.mvpbase.BaseActivity;
import in.arjsna.audiorecorder.settings.SettingsFragment;

public class SettingsActivity extends BaseActivity {
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preferences);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(R.string.action_settings);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
    }

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, new SettingsFragment())
        .commit();
  }
}
