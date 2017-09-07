package in.arjsna.voicerecorder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.fragments.PlayListFragment;

public class PlayListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_list);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.record_list_container, PlayListFragment.newInstance())
          .commit();
    }
  }
}
