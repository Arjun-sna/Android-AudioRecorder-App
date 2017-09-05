package in.arjsna.voicerecorder.recording;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.activities.MainActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class AudioRecordService extends Service {
  private static final String LOG_TAG = "RecordingService";

  private long mStartingTimeMillis = 0;
  private long mElapsedMillis = 0;
  private long mElapsedSeconds = 0;
  private static final SimpleDateFormat mTimerFormat =
      new SimpleDateFormat("hh:mm:ss", Locale.getDefault());

  private AudioRecorder audioRecorder;
  private AudioRecordingDbmHandler handler;
  private ServiceBinder mIBinder;
  private NotificationManager mNotificationManager;
  private static int NOTIFY_ID = 100;

  @Override public IBinder onBind(Intent intent) {
    return mIBinder;
  }

  public boolean isRecording() {
    return audioRecorder.isRecording();
  }

  @Override public void onCreate() {
    super.onCreate();
    mIBinder = new ServiceBinder();
    audioRecorder = new AudioRecorder();
    handler = new AudioRecordingDbmHandler();
    handler.addRecorder(audioRecorder);
    mTimerFormat.setTimeZone(TimeZone.getTimeZone("IST"));
    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public AudioRecordingDbmHandler getHandler() {
    return handler;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    startRecording();
    startForeground(NOTIFY_ID, createNotification(new AudioRecorder.RecordTime()));
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (isRecording()) {
      stopRecodingAndRelease();
    }
  }

  private void stopRecodingAndRelease() {
    audioRecorder.finishRecord();
    handler.stop();
  }

  public void startRecording() {
    audioRecorder.startRecord();
    handler.startDbmThread();
    audioRecorder.subscribeTimer(this::updateNotification);
  }

  private void updateNotification(AudioRecorder.RecordTime recordTime) {
    mElapsedMillis = recordTime.millis;
    mNotificationManager.notify(NOTIFY_ID, createNotification(recordTime));
  }

  private Notification createNotification(AudioRecorder.RecordTime recordTime) {
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_launcher_background)
            .setContentTitle(getString(R.string.notification_recording))
            .setContentText(String.format("%02d:%02d:%02d", recordTime.hours, recordTime.minutes,
                recordTime.seconds))
            .setOngoing(true);

    mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
        new Intent[] { new Intent(getApplicationContext(), MainActivity.class) }, 0));

    return mBuilder.build();
  }

  public void pauseRecord() {
    audioRecorder.pauseRecord();
  }

  public void resumeRecord() {
    audioRecorder.resumeRecord();
  }

  public long getElapsedTime() {
    return mElapsedMillis;
  }

  public class ServiceBinder extends Binder {
    public AudioRecordService getService() {
      return AudioRecordService.this;
    }
  }
}
