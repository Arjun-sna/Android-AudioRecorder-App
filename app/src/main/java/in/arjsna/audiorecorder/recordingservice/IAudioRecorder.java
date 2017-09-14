package in.arjsna.audiorecorder.recordingservice;

/**
 * Interface for audio recorder
 */
interface IAudioRecorder {
  void startRecord(int recordSampleRate);

  void finishRecord();

  void pauseRecord();

  void resumeRecord();

  boolean isRecording();
}
