package in.arjsna.audiorecorder.di.components;

import android.content.Context;
import dagger.Component;
import in.arjsna.audiorecorder.AudioRecorderApp;
import in.arjsna.audiorecorder.di.ApplicationContext;
import in.arjsna.audiorecorder.di.modules.ApplicationModule;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
  void inject(AudioRecorderApp audioRecorderApp);

  @ApplicationContext Context context();
}
