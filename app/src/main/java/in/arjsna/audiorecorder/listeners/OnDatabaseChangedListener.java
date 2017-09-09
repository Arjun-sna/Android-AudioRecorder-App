package in.arjsna.audiorecorder.listeners;

public interface OnDatabaseChangedListener {
  void onNewDatabaseEntryAdded();

  void onDatabaseEntryRenamed();
}