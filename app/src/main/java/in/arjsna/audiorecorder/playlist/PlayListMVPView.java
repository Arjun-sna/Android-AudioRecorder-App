package in.arjsna.audiorecorder.playlist;

import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.mvpbase.IMVPView;
import java.util.ArrayList;

public interface PlayListMVPView extends IMVPView {
  void showData(ArrayList<RecordingItem> recordingItems);

  void setRecordingListVisible();

  void setRecordingListInVisible();

  void setEmptyLabelVisible();

  void setEmptyLabelInVisible();

  void startWatchingForFileChanges();

  void stopWatchingForFileChanges();

  void notifyListItemChange(Integer position);

  void showError(String message);
}
