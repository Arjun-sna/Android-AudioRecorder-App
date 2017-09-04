package in.arjsna.voicerecorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.fragments.FileViewerFragment;
import in.arjsna.voicerecorder.fragments.RecordFragment;

public class MainActivity extends AppCompatActivity {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.main_container, RecordFragment.newInstance())
          .commit();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
