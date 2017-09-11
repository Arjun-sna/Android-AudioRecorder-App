package in.arjsna.audiorecorder.di.components;

import dagger.Component;
import in.arjsna.audiorecorder.activities.MainActivity;
import in.arjsna.audiorecorder.activities.PlayListActivity;
import in.arjsna.audiorecorder.activities.SettingsActivity;
import in.arjsna.audiorecorder.di.PerActivity;
import in.arjsna.audiorecorder.di.modules.ActivityModule;
import in.arjsna.audiorecorder.fragments.PlayListFragment;
import in.arjsna.audiorecorder.fragments.RecordFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
  void inject(MainActivity mainActivity);

  void inject(SettingsActivity mainActivity);

  void inject(PlayListActivity mainActivity);

  void inject(PlayListFragment playListFragment);

  void inject(RecordFragment recordFragment);
}
