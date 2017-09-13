package in.arjsna.audiorecorder.audiorecording;

import in.arjsna.audiorecorder.mvpbase.IMVPView;
import in.arjsna.audiorecorder.recording.AudioRecordingDbmHandler;

public interface AudioRecordMVPView extends IMVPView {
  void updateChronometer(String text);
  void togglePauseButton();
  void toggleRecordButton();
  void linkGLViewToHandler(AudioRecordingDbmHandler dbmHandler);
  void setPauseButtonVisible();
  void setPauseButtonInVisible();
  void setScreenOnFlag();
  void clearScreenOnFlag();
}
