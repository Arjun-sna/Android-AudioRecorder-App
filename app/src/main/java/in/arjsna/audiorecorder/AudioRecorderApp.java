package in.arjsna.audiorecorder;

import android.app.Application;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.di.components.ApplicationComponent;
import in.arjsna.audiorecorder.di.components.DaggerApplicationComponent;
import in.arjsna.audiorecorder.di.modules.ApplicationModule;

public class AudioRecorderApp extends Application {
  private ApplicationComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();
    Hawk.init(getApplicationContext()).build();
    applicationComponent =
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    applicationComponent.inject(this);
  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }
}
