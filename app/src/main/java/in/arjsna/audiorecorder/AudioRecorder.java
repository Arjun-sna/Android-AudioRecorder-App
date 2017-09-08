package in.arjsna.audiorecorder;

import android.app.Application;
import com.orhanobut.hawk.Hawk;

public class AudioRecorder extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Hawk.init(getApplicationContext()).build();
  }
}
