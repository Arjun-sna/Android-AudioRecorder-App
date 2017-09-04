package in.arjsna.voicerecorder.recording;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.activities.MainActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecordService extends Service {
  private static final String LOG_TAG = "RecordingService";

  private long mStartingTimeMillis = 0;
  private long mElapsedMillis = 0;
  private int mElapsedSeconds = 0;
  private OnTimerChangedListener onTimerChangedListener = null;
  private static final SimpleDateFormat mTimerFormat =
      new SimpleDateFormat("mm:ss", Locale.getDefault());

  private Timer mTimer = null;
  private TimerTask mIncrementTimerTask = null;
  private AudioRecorder audioRecorder;
  private AudioRecordingDbmHandler handler;
  private ServiceBinder mIBinder;

  @Override public IBinder onBind(Intent intent) {
    return mIBinder;
  }

  public boolean isRecording() {
    return audioRecorder.isRecording();
  }

  public interface OnTimerChangedListener {
    void onTimerChanged(int seconds);
  }

  @Override public void onCreate() {
    super.onCreate();
    mIBinder = new ServiceBinder();
    audioRecorder = new AudioRecorder();
    handler = new AudioRecordingDbmHandler();
    handler.addRecorder(audioRecorder);
  }

  public AudioRecordingDbmHandler getHandler() {
    return handler;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    startRecording();
    startForeground(100, createNotification());
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    stopRecodingAndRelease();
  }

  private void stopRecodingAndRelease() {
    audioRecorder.finishRecord();
    handler.stop();
  }

  public void startRecording() {
    audioRecorder.startRecord();
    handler.startRecordThread();
  }

  //TODO:
  private Notification createNotification() {
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_launcher_background)
            .setContentTitle(getString(R.string.notification_recording))
            .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
            .setOngoing(true);

    mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
        new Intent[] { new Intent(getApplicationContext(), MainActivity.class) }, 0));

    return mBuilder.build();
  }

  public class ServiceBinder extends Binder {
    public AudioRecordService getService() {
      return AudioRecordService.this;
    }
  }
}
