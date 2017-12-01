package in.arjsna.audiorecorder.di;

import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.audiorecording.AudioRecordMVPView;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenter;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenterImpl;
import in.arjsna.audiorecorder.di.scopes.FragmentScope;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by arjun on 12/1/17.
 */

@Module
class RecordFragmentModule {

  @Provides
  @FragmentScope
  AudioRecordPresenter<AudioRecordMVPView> provideAudioRecordPresenter(
      AudioRecordPresenterImpl<AudioRecordMVPView> audioRecordPresenter) {
    return audioRecordPresenter;
  }

  @Provides
  @FragmentScope
  CompositeDisposable provideCompositeDisposable() {
    return new CompositeDisposable();
  }
}
