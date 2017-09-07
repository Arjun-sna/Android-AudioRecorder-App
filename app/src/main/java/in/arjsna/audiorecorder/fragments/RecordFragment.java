package in.arjsna.audiorecorder.fragments;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import in.arjsna.audiorecorder.AppConstants;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.activities.PlayListActivity;
import in.arjsna.audiorecorder.activities.SettingsActivity;
import in.arjsna.audiorecorder.audiovisualization.AudioVisualization;
import in.arjsna.audiorecorder.recording.AudioRecordService;
import in.arjsna.audiorecorder.recording.AudioRecorder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
  private static final String LOG_TAG = RecordFragment.class.getSimpleName();
  private FloatingActionButton mRecordButton = null;
  private FloatingActionButton mPauseButton = null;
  private AudioVisualization audioVisualization;

  private boolean mIsRecording = false;
  private boolean mIsRecordingPaused = true;

  private TextView chronometer;
  private boolean mIsServiceBound = false;
  private AudioRecordService mAudioRecordService;
  private ObjectAnimator alphaAnimator;
  private FloatingActionButton mSettingsButton;
  private FloatingActionButton mPlayListBtn;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment Record_Fragment.
   */
  public static RecordFragment newInstance() {
    return new RecordFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View recordView = inflater.inflate(R.layout.fragment_record, container, false);
    initViews(recordView);
    bindEvents();
    bindToService();
    return recordView;
  }

  private void bindEvents() {
    RxView.clicks(mRecordButton).subscribe(o -> onChangeRecord());
    RxView.clicks(mSettingsButton).subscribe(o -> {
      Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
      startActivity(settingsIntent);
    });
    RxView.clicks(mPlayListBtn).subscribe(o -> {
      Intent viewPlayListIntent = new Intent(getActivity(), PlayListActivity.class);
      startActivity(viewPlayListIntent);
    });

    mPauseButton.setOnClickListener(v -> {
      mIsRecordingPaused = !mIsRecordingPaused;
      onPauseRecord();
    });
  }

  private void initViews(View recordView) {
    chronometer = (TextView) recordView.findViewById(R.id.chronometer);
    setChronometer(new AudioRecorder.RecordTime());

    audioVisualization = (AudioVisualization) recordView.findViewById(R.id.visualizer_view);

    mSettingsButton = (FloatingActionButton) recordView.findViewById(R.id.settings_btn);
    mPlayListBtn = (FloatingActionButton) recordView.findViewById(R.id.play_list_btn);
    mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
    mRecordButton.setImageResource(
        mIsRecording ? R.drawable.ic_media_stop : R.drawable.ic_media_record);
    mPauseButton = (FloatingActionButton) recordView.findViewById(R.id.btnPause);
    mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts

    alphaAnimator =
        ObjectAnimator.ofObject(chronometer, "alpha", new FloatEvaluator(), 0.2f);
    alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
    alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
  }

  private void onChangeRecord() {
    Intent intent = new Intent(getActivity(), AudioRecordService.class);
    if (!mIsRecording) {
      mIsRecording = true;
      mRecordButton.setImageResource(R.drawable.ic_media_stop);
      getActivity().startService(intent);
      bindToService();
      mPauseButton.setVisibility(View.VISIBLE);
      getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      stopRecording();
    }
  }

  private void stopRecording() {
    Intent intent = new Intent(getContext(), AudioRecordService.class);
    mIsRecording = false;
    mIsRecordingPaused = false;
    mRecordButton.setImageResource(R.drawable.ic_media_record);
    getActivity().stopService(intent);
    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    unbindService();
    setChronometer(new AudioRecorder.RecordTime());
    mPauseButton.setVisibility(View.GONE);
    togglePauseBtn();
  }

  BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (!intent.hasExtra(AppConstants.ACTION_IN_SERVICE)) return;
      String actionExtra = intent.getStringExtra(AppConstants.ACTION_IN_SERVICE);
      switch (actionExtra) {
        case AppConstants.ACTION_PAUSE:
          mIsRecordingPaused = true;
          togglePauseBtn();
          break;
        case AppConstants.ACTION_RESUME:
          mIsRecordingPaused = false;
          togglePauseBtn();
          break;
        case AppConstants.ACTION_STOP:
          stopRecording();
          break;
      }
    }
  };

  private void unRegisterLocalBroadCastReceiver() {
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(stopServiceReceiver);
  }

  private void registerLocalBroadCastReceiver() {
    LocalBroadcastManager.getInstance(getActivity())
        .registerReceiver(stopServiceReceiver, new IntentFilter(AppConstants.ACTION_IN_SERVICE));
  }

  private void bindToService() {
    Intent intent = new Intent(getActivity(), AudioRecordService.class);
    getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    registerLocalBroadCastReceiver();
  }

  private Disposable timerDisposable;
  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mAudioRecordService =
          ((AudioRecordService.ServiceBinder) iBinder).getService();
      Log.i("Tesing", " " + mAudioRecordService.isRecording() + " recording");
      mIsRecording = mAudioRecordService.isRecording();
      mIsServiceBound = true;
      if (mIsRecording) {
        mIsRecordingPaused = mAudioRecordService.isPaused();
        onPauseRecord();
        audioVisualization.linkTo(mAudioRecordService.getHandler());
        mRecordButton.setImageResource(R.drawable.ic_media_stop);
        timerDisposable = mAudioRecordService.subscribeForTimer(recordTimeConsumer);
      } else {
        unbindService();
      }
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
    }
  };

  Consumer<AudioRecorder.RecordTime> recordTimeConsumer = this::setChronometer;

  private void setChronometer(AudioRecorder.RecordTime recordTime) {
    chronometer.setText(
        String.format(Locale.getDefault(), getString(R.string.record_time_format), recordTime.hours,
            recordTime.minutes,
            recordTime.seconds));
  }

  private void onPauseRecord() {
    mPauseButton.setVisibility(View.VISIBLE);
    if (mIsRecordingPaused) {
      mAudioRecordService.pauseRecord();
      setAsResumeBtn();
    } else {
      mAudioRecordService.resumeRecord();
      setAsPauseBtn();
    }
  }

  private void setAsPauseBtn() {
    alphaAnimator.cancel();
    chronometer.setAlpha(1.0f);
    mPauseButton.setImageResource(R.drawable.ic_media_pause);
  }

  private void setAsResumeBtn() {
    alphaAnimator.start();
    mPauseButton.setImageResource(R.drawable.ic_media_record);
  }

  private void togglePauseBtn() {
    if (mIsRecordingPaused) {
      setAsResumeBtn();
    } else {
      setAsPauseBtn();
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    audioVisualization.release();
    unbindService();
  }

  private void unbindService() {
    unRegisterLocalBroadCastReceiver();
    if (timerDisposable != null) {
      timerDisposable.dispose();
    }
    if (mIsServiceBound) {
      mIsServiceBound = false;
      getActivity().unbindService(serviceConnection);
    }
  }
}