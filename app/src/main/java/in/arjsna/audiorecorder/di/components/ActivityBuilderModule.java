package in.arjsna.audiorecorder.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.audiorecorder.activities.MainActivity;
import in.arjsna.audiorecorder.activities.PlayListActivity;
import in.arjsna.audiorecorder.activities.SettingsActivity;
import in.arjsna.audiorecorder.di.modules.ActivityModule;
import in.arjsna.audiorecorder.di.scopes.ActivityScope;

/**
 * Created by arjun on 12/1/17.
 */

@Module
abstract class ActivityBuilderModule {
  @ActivityScope
  @ContributesAndroidInjector(modules = {ActivityModule.class, RecordFragmentBuilderModule.class})
  abstract MainActivity contributeMainActivity();

  @ContributesAndroidInjector(modules = {PlaybackFragmentBuilderModule.class})
  abstract PlayListActivity contributePlayListActivity();

  @ContributesAndroidInjector()
  abstract SettingsActivity contributeSettingsActivity();
}
