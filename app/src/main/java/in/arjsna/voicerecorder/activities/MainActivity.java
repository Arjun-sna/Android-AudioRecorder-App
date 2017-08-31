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

  private TabLayout tabs;
  private ViewPager pager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initViews();
    bindEvents();
  }

  private void initViews() {
    pager = (ViewPager) findViewById(R.id.pager);
    pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
    pager.setOffscreenPageLimit(2);
    tabs = (TabLayout) findViewById(R.id.tabs);
    tabs.setupWithViewPager(pager);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
    setSupportActionBar(toolbar);
  }

  private void bindEvents() {
    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override public void onTabReselected(TabLayout.Tab tab) {

      }
    });
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

  public class MyAdapter extends FragmentPagerAdapter {
    private String[] titles = {
        getString(R.string.tab_title_record), getString(R.string.tab_title_saved_recordings)
    };

    public MyAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0: {
          return RecordFragment.newInstance(position);
        }
        case 1: {
          return FileViewerFragment.newInstance(position);
        }
      }
      return null;
    }

    @Override public int getCount() {
      return titles.length;
    }

    @Override public CharSequence getPageTitle(int position) {
      return titles[position];
    }
  }

  public MainActivity() {
  }
}
