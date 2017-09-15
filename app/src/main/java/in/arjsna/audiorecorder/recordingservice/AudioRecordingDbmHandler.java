package in.arjsna.audiorecorder.recordingservice;

import android.util.Log;
import in.arjsna.audiorecorder.audiovisualization.DbmHandler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import javax.inject.Inject;

public class AudioRecordingDbmHandler extends DbmHandler<byte[]> {

  private static final float MAX_DB_VALUE = 170;

  private float[] dbs;
  private float[] allAmps;
  private Disposable disposableSubscriber;

  @Inject
  public AudioRecordingDbmHandler() {
  }

  @Override protected void onDataReceivedImpl(byte[] bytes, int layersCount, float[] dBmArray,
      float[] ampsArray) {
    if (layersCount < 1) return;
    final int bytesPerSample = 2; // As it is 16bit PCM
    final double amplification = 100.0; // choose a number as you like
    Complex[] fft = new Complex[bytes.length / bytesPerSample];
    for (int index = 0, floatIndex = 0; index < bytes.length - bytesPerSample + 1;
        index += bytesPerSample, floatIndex++) {
      double sample = 0;
      for (int b = 0; b < bytesPerSample; b++) {
        int v = bytes[index + b];
        if (b < bytesPerSample - 1) {
          v &= 0xFF;
        }
        sample += v << (b * 8);
      }
      double sample32 = amplification * (sample / 32768.0);
      fft[floatIndex] = new Complex(sample32, 0);
    }
    fft = FFT.fft(fft);
    // calculate dBs and amplitudes
    int dataSize = fft.length / 2 - 1;
    if (dbs == null || dbs.length != dataSize) {
      dbs = new float[dataSize];
    }
    if (allAmps == null || allAmps.length != dataSize) {
      allAmps = new float[dataSize];
    }

    for (int i = 0; i < dataSize; i++) {
      dbs[i] = (float) fft[i].abs();
      float k = 1;
      if (i == 0 || i == dataSize - 1) {
        k = 2;
      }
      float re = (float) fft[2 * i].re();
      float im = (float) fft[2 * i + 1].im();
      float sqMag = re * re + im * im;
      allAmps[i] = (float) (k * Math.sqrt(sqMag) / dataSize);
    }
    int size = dbs.length / layersCount;
    for (int i = 0; i < layersCount; i++) {
      int index = (int) ((i + 0.5f) * size);
      float db = dbs[index];
      float amp = allAmps[index];
      dBmArray[i] = db > MAX_DB_VALUE ? 1 : db / MAX_DB_VALUE;
      ampsArray[i] = amp;
    }
  }

  @Override public void startDbmThread() {
    disposableSubscriber = audioRecorder.getAudioDataFlowable()
        .onBackpressureLatest()
        .observeOn(Schedulers.newThread())
        .subscribeWith(new DisposableSubscriber<byte[]>() {
          @Override public void onNext(byte[] moreData) {
            if (moreData != null && moreData.length > 0) {
              onDataReceived(moreData);
            }
          }

          @Override public void onError(Throwable t) {
            Log.i("Visual Error ", t.getMessage() + " ");
          }

          @Override public void onComplete() {
            Log.i("Visualise ", "complete");
          }
        });
  }

  public void stop() {
    if (disposableSubscriber != null) {
      disposableSubscriber.dispose();
    }
    calmDownAndStopRendering();
  }
}