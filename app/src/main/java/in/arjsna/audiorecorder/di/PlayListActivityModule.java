package in.arjsna.audiorecorder.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.activities.PlayListActivity;
import in.arjsna.audiorecorder.di.qualifiers.ActivityContext;
import in.arjsna.audiorecorder.di.scopes.ActivityScope;

/**
 * Created by arjun on 12/1/17.
 */

@Module
class PlayListActivityModule {
  @Provides
  @ActivityContext
  @ActivityScope
  Context provideActivityContext(PlayListActivity activity) {
    return activity;
  }
}
