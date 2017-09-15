package in.arjsna.audiorecorder.playlist;

import in.arjsna.audiorecorder.db.RecordingItem;

interface ListItemEventsListener {
  void onItemLongClick(int position, RecordingItem recordingItem);

  void onItemClick(int position, RecordingItem recordingItem);
}
