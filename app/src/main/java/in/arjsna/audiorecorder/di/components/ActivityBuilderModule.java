package in.arjsna.audiorecorder.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.audiorecorder.activities.MainActivity;
import in.arjsna.audiorecorder.activities.SettingsActivity;

/**
 * Created by arjun on 12/1/17.
 */

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = {RecordFragmentBuilderModule.class})
  abstract MainActivity contributeMainActivity();

  @ContributesAndroidInjector(modules = {PlaybackFragmentBuilderModule.class})
  abstract MainActivity contributePlayListActivity();

  @ContributesAndroidInjector()
  abstract SettingsActivity contributeSettinsActivity();
}
