package in.arjsna.audiorecorder.di.components;

import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.di.scopes.ActivityScope;
import in.arjsna.audiorecorder.playlist.PlayListMVPView;
import in.arjsna.audiorecorder.playlist.PlayListPresenter;
import in.arjsna.audiorecorder.playlist.PlayListPresenterImpl;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by arjun on 12/1/17.
 */

@Module
class PlaybackFragmentModule {
  @Provides
  @ActivityScope
  PlayListPresenter<PlayListMVPView> providePlayListPresenter(PlayListPresenterImpl<PlayListMVPView> playListPresenter) {
    return playListPresenter;
  }

  @Provides
  @ActivityScope
  CompositeDisposable provideCompositeDisposable() {
    return new CompositeDisposable();
  }
}
