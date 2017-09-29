package in.arjsna.audiorecorder.di.modules;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.audiorecording.AudioRecordMVPView;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenter;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenterImpl;
import in.arjsna.audiorecorder.di.qualifiers.ActivityContext;
import in.arjsna.audiorecorder.di.scopes.ActivityScope;
import in.arjsna.audiorecorder.playlist.PlayListMVPView;
import in.arjsna.audiorecorder.playlist.PlayListPresenter;
import in.arjsna.audiorecorder.playlist.PlayListPresenterImpl;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {
  private AppCompatActivity appCompatActivity;

  public ActivityModule(AppCompatActivity appCompatActivity) {
    this.appCompatActivity = appCompatActivity;
  }

  @Provides
  @ActivityContext
  @ActivityScope
  AppCompatActivity provideActivityContext() {
    return appCompatActivity;
  }

  @Provides
  @ActivityScope
  CompositeDisposable provideCompositeDisposable() {
    return new CompositeDisposable();
  }

  @Provides
  @ActivityScope
  LinearLayoutManager provideLinearLayoutManager(@ActivityContext AppCompatActivity context) {
    return new LinearLayoutManager(context);
  }

  @Provides
  @ActivityScope
  AudioRecordPresenter<AudioRecordMVPView> provideAudioRecordPresenter(
      AudioRecordPresenterImpl<AudioRecordMVPView> audioRecordPresenter) {
    return audioRecordPresenter;
  }

  @Provides
  @ActivityScope
  PlayListPresenter<PlayListMVPView> providePlayListPresenter(
      PlayListPresenterImpl<PlayListMVPView> playListPresenter) {
    return playListPresenter;
  }
}
