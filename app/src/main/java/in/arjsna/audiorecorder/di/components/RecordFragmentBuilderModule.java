package in.arjsna.audiorecorder.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.audiorecorder.audiorecording.RecordFragment;

/**
 * Created by arjun on 12/1/17.
 */

@Module
abstract class RecordFragmentBuilderModule {
  @ContributesAndroidInjector(modules = {RecordFragmentModule.class})
  abstract RecordFragment contributeRecordFragment();
}
