package in.arjsna.audiorecorder.mvpbase;

import in.arjsna.audiorecorder.db.RecordingItem;

public interface IMVPPresenter<V extends IMVPView> {
  void onAttach(V view);

  void onDetach();
}
