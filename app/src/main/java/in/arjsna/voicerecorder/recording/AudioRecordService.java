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
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecordService extends Service {
  private static final String LOG_TAG = "RecordingService";

  private String mFileName = null;
  private String mFilePath = null;

  private long mStartingTimeMillis = 0;
  private long mElapsedMillis = 0;
  private int mElapsedSeconds = 0;
  private OnTimerChangedListener onTimerChangedListener = null;
  private static final SimpleDateFormat mTimerFormat =
      new SimpleDateFormat("mm:ss", Locale.getDefault());

  private Timer mTimer = null;
  private TimerTask mIncrementTimerTask = null;
  private boolean isServiceInProgress = false;
  private AudioRecorder audioRecorder;
  private AudioRecordingDbmHandler handler;
  private ServiceBinder mIBinder;
  private File currentFile;
  private FileOutputStream currentFileOutStream;
  //private MediaSaveHelper mediaSaveHelper;

  @Override public IBinder onBind(Intent intent) {
    return mIBinder;
  }

  public boolean isRecording() {
    return isServiceInProgress;
  }

  public interface OnTimerChangedListener {
    void onTimerChanged(int seconds);
  }

  @Override public void onCreate() {
    super.onCreate();
    mIBinder = new ServiceBinder();
    audioRecorder = new AudioRecorder();
    //mediaSaveHelper = new MediaSaveHelper();
    handler = new AudioRecordingDbmHandler();
    //audioRecorder.addRecordingCallback(mediaSaveHelper);
    handler.addRecorder(audioRecorder);
  }

  private byte[] short2byte(short[] sData) {
    int shortArrsize = sData.length;
    byte[] bytes = new byte[shortArrsize * 2];

    for (int i = 0; i < shortArrsize; i++) {
      bytes[i * 2] = (byte) (sData[i] & 0x00FF);
      bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
      sData[i] = 0;
    }
    return bytes;

  }

  public AudioRecordingDbmHandler getHandler() {
    return handler;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    isServiceInProgress = true;
    startRecording();
    startForeground(100, createNotification());
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    isServiceInProgress = false;
    stopRecodingAndRelease();
  }

  private void stopRecodingAndRelease() {
    audioRecorder.finishRecord();
    handler.stop();
  }

  public void startRecording() {
    //mediaSaveHelper.createNewFile();
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
