package in.arjsna.audiorecorder.playlist;

import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.mvpbase.IMVPPresenter;

public interface PlayListPresenter<V extends PlayListMVPView> extends IMVPPresenter<V> {
  void onViewInitialised();

  void renameFile(RecordingItem recordingItem, int position, String value);

  void deleteFile(RecordingItem recordingItem, int position);

  void onListItemClicked(int position, RecordingItem recordingItem);
}
