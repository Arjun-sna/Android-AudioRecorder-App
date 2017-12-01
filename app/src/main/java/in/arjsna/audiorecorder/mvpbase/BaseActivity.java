package in.arjsna.audiorecorder.mvpbase;

import android.os.Bundle;
import dagger.android.AndroidInjection;
import in.arjsna.audiorecorder.theme.ThemedActivity;

public abstract class BaseActivity extends ThemedActivity implements IMVPView {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidInjection.inject(this);
  }
}
