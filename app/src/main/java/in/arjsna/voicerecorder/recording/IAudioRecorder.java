package in.arjsna.voicerecorder.recording;

/**
 * Interface for audio recorder
 */
interface IAudioRecorder {
  void startRecord();

  void finishRecord();

  void pauseRecord();

  void resumeRecord();

  boolean isRecording();
}
