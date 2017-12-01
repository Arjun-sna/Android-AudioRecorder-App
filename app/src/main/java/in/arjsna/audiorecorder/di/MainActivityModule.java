package in.arjsna.audiorecorder.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.activities.MainActivity;
import in.arjsna.audiorecorder.di.qualifiers.ActivityContext;
import in.arjsna.audiorecorder.di.scopes.ActivityScope;

@Module
public class MainActivityModule {
  @Provides
  @ActivityContext
  @ActivityScope
  Context provideActivityContext(MainActivity activity) {
    return activity;
  }
}
