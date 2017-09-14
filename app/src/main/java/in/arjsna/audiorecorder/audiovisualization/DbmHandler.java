package in.arjsna.audiorecorder.audiovisualization;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import in.arjsna.audiorecorder.recordingservice.AudioRecorder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Abstract class for converting your data to dBm values.
 * When you're have new portion of data, call {@link #onDataReceived(Object)} method.
 */
public abstract class DbmHandler<TData> {

  private static final long UPDATE_INTERVAL = 16;
  private int layersCount;
  private InnerAudioVisualization audioVisualization;
  private float[] dBmArray;
  private float[] ampsArray;
  private float[] emptyArray;
  private Timer timer;
  private boolean isVisualizationSetup = false;
  protected AudioRecorder audioRecorder;

  void setUp(@NonNull InnerAudioVisualization audioVisualization, int layersCount) {
    isVisualizationSetup = true;
    this.audioVisualization = audioVisualization;
    this.layersCount = layersCount;
    this.dBmArray = new float[layersCount];
    this.ampsArray = new float[layersCount];
    this.emptyArray = new float[layersCount];
  }

  /**
   * Call this method when your data is available for conversion.
   *
   * @param data any data
   */
  public final void onDataReceived(TData data) {
    onDataReceivedImpl(data, layersCount, dBmArray, ampsArray);
    if (isVisualizationSetup) {
      audioVisualization.onDataReceived(dBmArray, ampsArray);
      startRendering();
    }
  }

  /**
   * Start rendering thread.
   */
  protected final void startRendering() {
    cancelTimer();
    audioVisualization.startRendering();
  }

  /**
   * Stop rendering thread.
   */
  protected final void stopRendering() {
    cancelTimer();
    if (audioVisualization != null) {
      audioVisualization.stopRendering();
    }
  }

  /**
   * Post empty values to renderer and stop rendering thread after waves calm down.
   */
  protected final void calmDownAndStopRendering() {
    if (timer == null) {
      timer = new Timer("Stop Rendering Timer");
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override public void run() {
          if (audioVisualization != null) {
            audioVisualization.onDataReceived(emptyArray, emptyArray);
          }
        }
      }, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }
    if (isVisualizationSetup) {
      audioVisualization.calmDownListener(this::stopRendering);
    }
  }

  /**
   * Cancel timer posting empty values.
   */
  private void cancelTimer() {
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
    }
  }

  /**
   * Called after {@link AudioVisualization#onResume()} call.
   */
  public void onResume() {

  }

  /**
   * Called after {@link AudioVisualization#onPause()} call.
   */
  public void onPause() {

  }

  /**
   * Called after {@link AudioVisualization#release()} ()} call.
   */
  @CallSuper public void release() {
    isVisualizationSetup = false;
    dBmArray = null;
    ampsArray = null;
    audioVisualization = null;
  }

  /**
   * Implement your own data conversion.
   *
   * @param data any data
   * @param layersCount layers count
   * @param dBmArray array of normalized (in range [0..1]) dBm values that should be populated by
   * you.
   * Array size is equals to {@code layersCount} value.
   * @param ampsArray array of amplitude values that should be populated by you.
   * Array size is equals to {@code layersCount} value.<br/><br/>
   * This values affect the appearance of bubbles. If new amplitude value is greater
   * than previous value and normalized dBm value is greater than 0.25, bubbles will appear on
   * screen.
   * In case if amplitude is less than previous value, exponential smoothing (Holt - Winters)
   * used for smoothing amplitude values.
   */
  protected abstract void onDataReceivedImpl(TData data, int layersCount, float[] dBmArray,
      float[] ampsArray);

  public void addRecorder(AudioRecorder audioRecorder) {
    this.audioRecorder = audioRecorder;
  }

  public abstract void startDbmThread();
}
