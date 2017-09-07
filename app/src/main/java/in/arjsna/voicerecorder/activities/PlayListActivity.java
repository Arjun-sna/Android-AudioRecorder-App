package in.arjsna.voicerecorder.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.fragments.PlayListFragment;

public class PlayListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(R.string.tab_title_saved_recordings);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
    }

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.record_list_container, PlayListFragment.newInstance())
          .commit();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }
}
