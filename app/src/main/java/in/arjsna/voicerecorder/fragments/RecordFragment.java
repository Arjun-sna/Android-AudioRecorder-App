package in.arjsna.voicerecorder.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.audiovisualization.AudioVisualization;
import in.arjsna.voicerecorder.recording.AudioRecordService;
import in.arjsna.voicerecorder.recording.AudioRecorder;
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
  private Button mPauseButton = null;
  private AudioVisualization audioVisualization;

  private boolean mIsRecording = false;
  private boolean mIsRecordingPaused = true;

  private TextView chronometer;
  private boolean mIsServiceBound = false;
  private AudioRecordService mAudioRecordService;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment Record_Fragment.
   */
  public static RecordFragment newInstance() {
    return new RecordFragment();
  }

  private boolean mIsRecordPaused = false;

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

    mPauseButton.setOnClickListener(v -> {
      mIsRecordingPaused = !mIsRecordingPaused;
      onPauseRecord();
    });
  }

  private void initViews(View recordView) {
    chronometer = (TextView) recordView.findViewById(R.id.chronometer);
    setChronometer(new AudioRecorder.RecordTime());

    audioVisualization = (AudioVisualization) recordView.findViewById(R.id.visualizer_view);

    mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
    mRecordButton.setImageResource(
        mIsRecording ? R.drawable.ic_media_stop : R.drawable.ic_mic_white_36dp);
    mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
    mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
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
      mIsRecording = false;
      mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
      getActivity().stopService(intent);
      getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      unbindService();
      setChronometer(new AudioRecorder.RecordTime());
      mPauseButton.setVisibility(View.GONE);
    }
  }

  private void bindToService() {
    Intent intent = new Intent(getActivity(), AudioRecordService.class);
    getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mAudioRecordService =
          ((AudioRecordService.ServiceBinder) iBinder).getService();
      Log.i("Tesing", " " + mAudioRecordService.isRecording() + " recording");
      mIsRecording = mAudioRecordService.isRecording();
      if (mIsRecording) {
        mIsServiceBound = true;
        mIsRecordingPaused = mAudioRecordService.isPaused();
        if (mIsRecordingPaused) {
          showResumeBtn();
        }
        audioVisualization.linkTo(mAudioRecordService.getHandler());
        mRecordButton.setImageResource(R.drawable.ic_media_stop);
        mAudioRecordService.subscribeForTimer(recordTimeConsumer);
      } else {
        mIsServiceBound = false;
        getActivity().unbindService(this);
      }
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
    }
  };

  private void showResumeBtn() {
    mPauseButton.setVisibility(View.VISIBLE);
    mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_white_36dp, 0, 0, 0);
    mPauseButton.setText("Resume");
  }

  Consumer<AudioRecorder.RecordTime> recordTimeConsumer = this::setChronometer;

  private void setChronometer(AudioRecorder.RecordTime recordTime) {
    chronometer.setText(
        String.format(Locale.getDefault(), "%02d:%02d:%02d", recordTime.hours, recordTime.minutes,
            recordTime.seconds));
  }

  private void onPauseRecord() {
    if (mIsRecordingPaused) {
      mAudioRecordService.pauseRecord();
      mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_white_36dp, 0, 0, 0);
      mPauseButton.setText("Resume");
    } else {
      mAudioRecordService.resumeRecord();
      mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause, 0, 0, 0);
      mPauseButton.setText("Pause");
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    audioVisualization.release();
    unbindService();
  }

  private void unbindService() {
    if (mIsServiceBound) {
      mIsServiceBound = false;
      getActivity().unbindService(serviceConnection);
    }
  }
}