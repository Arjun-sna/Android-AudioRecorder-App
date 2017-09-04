package in.arjsna.voicerecorder.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.audiovisualization.AudioVisualization;
import in.arjsna.voicerecorder.recording.AudioRecordService;
import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_POSITION = "position";
  private static final String LOG_TAG = RecordFragment.class.getSimpleName();

  //Recording controls
  private FloatingActionButton mRecordButton = null;
  private Button mPauseButton = null;
  private AudioVisualization audioVisualization;

  private boolean mIsRecording = false;
  private boolean mPauseRecording = true;

  long timeWhenPaused = 0; //stores time when user clicks pause button

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment Record_Fragment.
   */
  public static RecordFragment newInstance() {
    RecordFragment f = new RecordFragment();
    return f;
  }

  public RecordFragment() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View recordView = inflater.inflate(R.layout.fragment_record, container, false);
    initViews(recordView);
    bindEvents();
    tryToBindService();
    return recordView;
  }

  private void bindEvents() {
    mRecordButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onChangeRecord();
      }
    });

    mPauseButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onPauseRecord(mPauseRecording);
        mPauseRecording = !mPauseRecording;
      }
    });
  }

  private void initViews(View recordView) {
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
      // start recording
      mIsRecording = true;
      mRecordButton.setImageResource(R.drawable.ic_media_stop);
      Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
      File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
      if (!folder.exists()) {
        folder.mkdir();
      }
      getActivity().startService(intent);
      tryToBindService();
      getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      mIsRecording = false;
      mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
      getActivity().unbindService(serviceConnection);
      getActivity().stopService(intent);
      getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  private void tryToBindService() {
    Intent intent = new Intent(getActivity(), AudioRecordService.class);
    boolean b = getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    Log.i("Tesing", " " + b + " binding service");
    if (mIsRecording) {
      mRecordButton.setImageResource(R.drawable.ic_media_stop);
    }
  }

  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      AudioRecordService audioRecordService =
          ((AudioRecordService.ServiceBinder) iBinder).getService();
      audioVisualization.linkTo(audioRecordService.getHandler());
      Log.i("Tesing", " " + audioRecordService.isRecording() + " recording");
      mIsRecording = audioRecordService.isRecording();
      if (mIsRecording) {
        mRecordButton.setImageResource(R.drawable.ic_media_stop);
      }
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      audioVisualization.release();
    }
  };

  //TODO: implement pause recording
  private void onPauseRecord(boolean pause) {
    if (pause) {
      //pause recording
      mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_white_36dp, 0, 0, 0);
    } else {
      //resume recording
      mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause, 0, 0, 0);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    audioVisualization.release();
    if (mIsRecording) {
      getActivity().unbindService(serviceConnection);
    }
  }
}