package in.arjsna.audiorecorder.mvpbase;

import android.os.Bundle;
import in.arjsna.audiorecorder.AudioRecorderApp;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.di.components.DaggerActivityComponent;
import in.arjsna.audiorecorder.di.modules.ActivityModule;
import in.arjsna.audiorecorder.theme.ThemedActivity;

public abstract class BaseActivity extends ThemedActivity implements IMVPView {
  private ActivityComponent activityComponent;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityComponent = DaggerActivityComponent.builder()
        .activityModule(new ActivityModule(this))
        .applicationComponent(((AudioRecorderApp) getApplication()).getApplicationComponent())
        .build();
  }

  public ActivityComponent getActivityComponent() {
    return activityComponent;
  }
}
