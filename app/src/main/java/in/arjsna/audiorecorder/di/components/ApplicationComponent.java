package in.arjsna.audiorecorder.di.components;

import android.content.Context;
import dagger.Component;
import in.arjsna.audiorecorder.AudioRecorderApp;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.di.modules.ApplicationModule;
import in.arjsna.audiorecorder.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
  void inject(AudioRecorderApp audioRecorderApp);

  @ApplicationContext
  Context context();

  RecordItemDataSource getRecordItemDataSource();
}
