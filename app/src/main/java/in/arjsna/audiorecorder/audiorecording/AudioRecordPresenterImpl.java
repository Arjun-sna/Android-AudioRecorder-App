package in.arjsna.audiorecorder.audiorecording;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import in.arjsna.audiorecorder.AppConstants;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.di.ActivityContext;
import in.arjsna.audiorecorder.mvpbase.BasePresenter;
import in.arjsna.audiorecorder.recording.AudioRecordService;
import in.arjsna.audiorecorder.recording.AudioRecorder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.Locale;
import javax.inject.Inject;

public class AudioRecordPresenterImpl<V extends AudioRecordMVPView> extends BasePresenter<V>
    implements AudioRecordPresenter<V> {

  @Inject
  @ActivityContext
  public AppCompatActivity mContext;
  private boolean mIsRecording = false;
  private boolean mIsRecordingPaused = false;

  @Inject
  public AudioRecordPresenterImpl(CompositeDisposable compositeDisposable) {
    super(compositeDisposable);
  }

  @Override public void onToggleRecodingStatus() {
    Intent intent = new Intent(mContext, AudioRecordService.class);
    if (!mIsRecording) {
      mIsRecording = true;
      getAttachedView().toggleRecordButton();
      mContext.startService(intent);
      bindToService();
      getAttachedView().setPauseButtonVisible();
      getAttachedView().setScreenOnFlag();
    } else {
      stopRecording();
    }
  }

  @Override public void onTogglePauseStatus() {
    getAttachedView().setPauseButtonVisible();
    mIsRecordingPaused = !mIsRecordingPaused;
    if (mIsRecordingPaused) {
      mAudioRecordService.pauseRecord();
    } else {
      mAudioRecordService.resumeRecord();
    }
    getAttachedView().togglePauseButton();
  }

  @Override public boolean isRecording() {
    return mIsRecording;
  }

  @Override public boolean isPaused() {
    return mIsRecordingPaused;
  }

  @Override public void onAttach(V view) {
    super.onAttach(view);
    bindToService();
  }

  @Override public void onViewInitialised() {
    getAttachedView().updateChronometer(getChronometerText(new AudioRecorder.RecordTime()));
    getAttachedView().toggleRecordButton();
  }

  @Override public void onDestroy() {
    unbindService();
  }

  private void bindToService() {
    Intent intent = new Intent(mContext, AudioRecordService.class);
    mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    registerLocalBroadCastReceiver();
  }

  private void registerLocalBroadCastReceiver() {
    LocalBroadcastManager.getInstance(mContext)
        .registerReceiver(serviceUpdateReceiver, new IntentFilter(AppConstants.ACTION_IN_SERVICE));
  }

  private AudioRecordService mAudioRecordService;
  private boolean mIsServiceBound = false;
  private Disposable timerDisposable;
  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mAudioRecordService =
          ((AudioRecordService.ServiceBinder) iBinder).getService();
      Log.i("Tesing", " " + mAudioRecordService.isRecording() + " recording");
      mIsRecording = mAudioRecordService.isRecording();
      mIsServiceBound = true;
      if (mIsRecording) {
        mIsRecordingPaused = mAudioRecordService.isPaused();
        getAttachedView().setPauseButtonVisible();
        getAttachedView().togglePauseButton();
        getAttachedView().linkGLViewToHandler(mAudioRecordService.getHandler());
        getAttachedView().toggleRecordButton();
        timerDisposable = mAudioRecordService.subscribeForTimer(recordTimeConsumer);
      } else {
        unbindService();
      }
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
    }
  };

  private void unbindService() {
    unRegisterLocalBroadCastReceiver();
    if (timerDisposable != null) {
      timerDisposable.dispose();
    }
    if (mIsServiceBound) {
      mIsServiceBound = false;
      mContext.unbindService(serviceConnection);
    }
  }

  private void unRegisterLocalBroadCastReceiver() {
    LocalBroadcastManager.getInstance(mContext).unregisterReceiver(serviceUpdateReceiver);
  }

  private final BroadcastReceiver serviceUpdateReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (!intent.hasExtra(AppConstants.ACTION_IN_SERVICE)) return;
      String actionExtra = intent.getStringExtra(AppConstants.ACTION_IN_SERVICE);
      switch (actionExtra) {
        case AppConstants.ACTION_PAUSE:
          mIsRecordingPaused = true;
          getAttachedView().togglePauseButton();
          break;
        case AppConstants.ACTION_RESUME:
          mIsRecordingPaused = false;
          getAttachedView().togglePauseButton();
          break;
        case AppConstants.ACTION_STOP:
          stopRecording();
          break;
      }
    }
  };

  private void stopRecording() {
    Intent intent = new Intent(mContext, AudioRecordService.class);
    mIsRecording = false;
    mIsRecordingPaused = false;
    getAttachedView().toggleRecordButton();
    mContext.stopService(intent);
    getAttachedView().clearScreenOnFlag();
    unbindService();
    getAttachedView().updateChronometer(getChronometerText(new AudioRecorder.RecordTime()));
    getAttachedView().setPauseButtonInVisible();
    getAttachedView().togglePauseButton();
  }

  private final Consumer<AudioRecorder.RecordTime> recordTimeConsumer =
      recordTime -> getAttachedView().updateChronometer(getChronometerText(recordTime));

  private String getChronometerText(AudioRecorder.RecordTime recordTime) {
    return String.format(Locale.getDefault(), mContext.getString(R.string.record_time_format),
        recordTime.hours,
        recordTime.minutes,
        recordTime.seconds);
  }
}
