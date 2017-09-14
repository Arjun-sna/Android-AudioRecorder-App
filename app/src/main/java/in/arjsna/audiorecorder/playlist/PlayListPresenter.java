package in.arjsna.audiorecorder.playlist;

import in.arjsna.audiorecorder.mvpbase.IMVPPresenter;

public interface PlayListPresenter<V extends PlayListMVPView> extends IMVPPresenter<V> {
  void onViewInitialised();
}
