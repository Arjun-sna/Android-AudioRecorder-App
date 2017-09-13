package in.arjsna.audiorecorder.di.modules;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.adapters.PlayListAdapter;
import in.arjsna.audiorecorder.audiorecording.AudioRecordMVPView;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenter;
import in.arjsna.audiorecorder.audiorecording.AudioRecordPresenterImpl;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.di.ActivityContext;
import in.arjsna.audiorecorder.di.PerActivity;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;

@Module
public class ActivityModule {
  private AppCompatActivity appCompatActivity;

  public ActivityModule(AppCompatActivity appCompatActivity) {
    this.appCompatActivity = appCompatActivity;
  }

  @Provides
  @ActivityContext
  AppCompatActivity provideActivityContext() {
    return appCompatActivity;
  }

  @Provides
  CompositeDisposable provideCompositeDisposable() {
    return new CompositeDisposable();
  }

  @Provides
  LinearLayoutManager provideLinearLayoutManager(@ActivityContext AppCompatActivity context) {
    return new LinearLayoutManager(context);
  }

  @Provides
  PlayListAdapter providesPlayListAdapter(@ActivityContext AppCompatActivity context,
      RecordItemDataSource recordItemDataSource) {
    return new PlayListAdapter(context, recordItemDataSource, new ArrayList<RecordingItem>());
  }

  @Provides
  @PerActivity
  AudioRecordPresenter<AudioRecordMVPView> provideAudioRecordPresenter(
      AudioRecordPresenterImpl<AudioRecordMVPView> audioRecordPresenter) {
    return audioRecordPresenter;
  }
}
