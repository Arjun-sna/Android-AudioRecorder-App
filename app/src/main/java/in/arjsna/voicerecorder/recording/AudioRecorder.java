package in.arjsna.voicerecorder.recording;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for audio recording and saving as .wav
 */
public class AudioRecorder implements IAudioRecorder {

  public static final int RECORDER_STATE_FAILURE = -1;
  public static final int RECORDER_STATE_IDLE = 0;
  public static final int RECORDER_STATE_STARTING = 1;
  public static final int RECORDER_STATE_STOPPING = 2;
  public static final int RECORDER_STATE_BUSY = 3;

  private volatile int recorderState;

  private MediaSaveHelper mediaSaveHelper;

  private final Object recorderStateMonitor = new Object();

  private byte[] recordBuffer;

  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private Observable<RecordTime> timerObservable;

  private Long recordTimeSeconds;

  public AudioRecorder() {
    this.mediaSaveHelper = new MediaSaveHelper();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored") private void onRecordFailure() {
    recorderState = RECORDER_STATE_FAILURE;
    finishRecord();
  }

  @Override public void startRecord() {
    if (recorderState != RECORDER_STATE_IDLE) {
      return;
    }
    startTimer();
    try {
      recorderState = RECORDER_STATE_STARTING;

      startRecordThread();
    } catch (FileNotFoundException e) {
      onRecordFailure();
      e.printStackTrace();
    }
  }

  private void startTimer() {
    timerObservable = Observable.interval(1000, TimeUnit.MILLISECONDS).map(seconds -> {
      recordTimeSeconds = seconds;
      RecordTime recordTime = new RecordTime();
      recordTime.millis = seconds * 1000;
      recordTime.hours = seconds / (60 * 60);
      seconds = seconds % (60 * 60);
      recordTime.minutes = seconds / 60;
      seconds = seconds % 60;
      recordTime.seconds = seconds;
      return recordTime;
    }).subscribeOn(Schedulers.newThread());
  }

  private Flowable<byte[]> audioDataFlowable = Flowable.create(emitter -> {
    int bufferSize = 4 * 1024;

    AudioRecord recorder =
        new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.RECORDER_SAMPLE_RATE,
            Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING, bufferSize);
    mediaSaveHelper.createNewFile();

    try {
      if (recorderState == RECORDER_STATE_STARTING) {
        recorderState = RECORDER_STATE_BUSY;
      }
      recorder.startRecording();

      recordBuffer = new byte[bufferSize];
      do {
        int bytesRead = recorder.read(recordBuffer, 0, bufferSize);
        emitter.onNext(recordBuffer);
        if (bytesRead == 0) {
          Log.e(AudioRecorder.class.getSimpleName(), "error: " + bytesRead);
          onRecordFailure();
        }
      } while (recorderState == RECORDER_STATE_BUSY);
    } finally {
      recorder.release();
    }
    emitter.onComplete();
  }, BackpressureStrategy.DROP);

  private PublishProcessor<byte[]> publishProcessor = PublishProcessor.create();

  private void startRecordThread() throws FileNotFoundException {
    audioDataFlowable.subscribeOn(Schedulers.io()).subscribe(publishProcessor);
    compositeDisposable.add(publishProcessor.onBackpressureBuffer()
        .observeOn(Schedulers.io())
        .subscribeWith(new DisposableSubscriber<byte[]>() {
          @Override public void onNext(byte[] bytes) {
            mediaSaveHelper.onDataReady(recordBuffer);
          }

          @Override public void onError(Throwable t) {

          }

          @Override public void onComplete() {
            synchronized (recorderStateMonitor) {
              recorderState = RECORDER_STATE_IDLE;
              recorderStateMonitor.notifyAll();
            }
          }
        }));
  }

  @Override public void finishRecord() {
    int recorderStateLocal = recorderState;
    if (recorderStateLocal != RECORDER_STATE_IDLE) {
      synchronized (recorderStateMonitor) {
        recorderStateLocal = recorderState;
        if (recorderStateLocal == RECORDER_STATE_STARTING
            || recorderStateLocal == RECORDER_STATE_BUSY) {

          recorderStateLocal = recorderState = RECORDER_STATE_STOPPING;
        }

        do {
          try {
            if (recorderStateLocal != RECORDER_STATE_IDLE) {
              recorderStateMonitor.wait();
            }
          } catch (InterruptedException ignore) {
                        /* Nothing to do */
          }
          recorderStateLocal = recorderState;
        } while (recorderStateLocal == RECORDER_STATE_STOPPING);
      }
    }
    mediaSaveHelper.onRecordingStopped();
    compositeDisposable.dispose();
  }

  @Override public boolean isRecording() {
    return recorderState != RECORDER_STATE_IDLE;
  }

  public Flowable<byte[]> getAudioDataFlowable() {
    return publishProcessor;
  }

  public void subscribeTimer(Consumer<RecordTime> timerConsumer) {
    compositeDisposable.add(timerObservable.subscribe(timerConsumer));
  }

  public static class RecordTime {
    long seconds = 0;
    long minutes = 0;
    long hours = 0;
    long millis = 0;
  }
}
