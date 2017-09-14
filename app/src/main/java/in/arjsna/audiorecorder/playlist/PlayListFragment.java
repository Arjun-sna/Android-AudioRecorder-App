package in.arjsna.audiorecorder.playlist;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.adapters.PlayListAdapter;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.mvpbase.BaseFragment;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListFragment extends BaseFragment implements PlayListMVPView {
  private static final String LOG_TAG = "PlayListFragment";

  @Inject
  public PlayListAdapter mPlayListAdapter;
  @Inject
  public LinearLayoutManager llm;
  @Inject
  public PlayListPresenter<PlayListMVPView> playListPresenter;
  private RecyclerView mRecordingsListView;
  private TextView emptyListLabel;

  public static PlayListFragment newInstance() {
    return new PlayListFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityComponent activityComponent = getActivityComponent();
    if (activityComponent != null) {
      activityComponent.inject(this);
      playListPresenter.onAttach(this);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
    initViews(v);
    return v;
  }

  private void initViews(View v) {
    emptyListLabel = v.findViewById(R.id.empty_list_label);
    mRecordingsListView = v.findViewById(R.id.recyclerView);
    mRecordingsListView.setHasFixedSize(true);
    llm.setOrientation(LinearLayoutManager.VERTICAL);

    //newest to oldest order (database stores from oldest to newest)
    llm.setReverseLayout(true);
    llm.setStackFromEnd(true);

    mRecordingsListView.setLayoutManager(llm);
    mRecordingsListView.setItemAnimator(new DefaultItemAnimator());
    mRecordingsListView.setAdapter(mPlayListAdapter);
    playListPresenter.onViewInitialised();
  }

  private final FileObserver observer = new FileObserver(
      android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder") {
    // set up a file observer to watch this directory on sd card
    @Override public void onEvent(int event, String file) {
      if (event == FileObserver.DELETE) {
        // user deletes a recording file out of the app

        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
            + "/SoundRecorder"
            + file
            + "]";

        Log.d(LOG_TAG, "File deleted ["
            + android.os.Environment.getExternalStorageDirectory().toString()
            + "/SoundRecorder"
            + file
            + "]");

        // remove file from database and recyclerview
        mPlayListAdapter.removeOutOfApp(filePath);
      }
    }
  };

  @Override public void refreshTheme(ThemeHelper themeHelper) {

  }

  @Override public void onDestroy() {
    super.onDestroy();
    playListPresenter.onDetach();
  }

  @Override public void showData(ArrayList<RecordingItem> recordingItems) {
    mPlayListAdapter.addAllAndNotify(recordingItems);
  }

  @Override public void setRecordingListVisible() {
    mRecordingsListView.setVisibility(View.VISIBLE);
  }

  @Override public void setRecordingListInVisible() {
    mRecordingsListView.setVisibility(View.GONE);
  }

  @Override public void setEmptyLabelVisible() {
    emptyListLabel.setVisibility(View.VISIBLE);
  }

  @Override public void setEmptyLabelInVisible() {
    emptyListLabel.setVisibility(View.GONE);
  }

  @Override public void startWatchingForFileChanges() {
    observer.startWatching();
  }

  @Override public void stopWatchingForFileChanges() {
    observer.stopWatching();
  }
}




