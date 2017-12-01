package in.arjsna.audiorecorder.di.components;

import android.app.Application;
import android.content.Context;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import in.arjsna.audiorecorder.AudioRecorderApp;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.di.modules.ApplicationModule;
import in.arjsna.audiorecorder.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Singleton
@Component(modules = {ApplicationModule.class, AndroidInjectionModule.class, ActivityBuilderModule.class})
public interface ApplicationComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance Builder application(Application application);

    ApplicationComponent builder();
  }

  void inject(AudioRecorderApp audioRecorderApp);
}
