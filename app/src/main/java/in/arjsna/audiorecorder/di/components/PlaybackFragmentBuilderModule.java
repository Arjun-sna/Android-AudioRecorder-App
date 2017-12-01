package in.arjsna.audiorecorder.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.audiorecorder.playback.PlaybackFragment;

/**
 * Created by arjun on 12/1/17.
 */

@Module
abstract class PlaybackFragmentBuilderModule {
  @ContributesAndroidInjector(modules = {PlaybackFragmentModule.class})
  abstract PlaybackFragment contributePlaybackFragment();
}
