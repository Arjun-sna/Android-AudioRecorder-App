package in.arjsna.audiorecorder.recordingservice;

import android.media.AudioFormat;

public class Constants {
  public static final int RECORDER_BPP = 16;
  public static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
  public static final int RECORDER_SAMPLE_RATE_LOW = 8000;
  public static final int RECORDER_SAMPLE_RATE_HIGH = 44100;
  public static final int BUFFER_BYTES_ELEMENTS = 2048 * 2;
  public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  public static final int BUFFER_BYTES_PER_ELEMENT = RECORDER_AUDIO_ENCODING;
}
