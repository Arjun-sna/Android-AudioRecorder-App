package in.arjsna.audiorecorder.fragments;

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
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.mvpbase.BaseFragment;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListFragment extends BaseFragment {
  private static final String LOG_TAG = "PlayListFragment";

  @Inject
  public PlayListAdapter mPlayListAdapter;
  @Inject
  public RecordItemDataSource recordItemDataSource;
  @Inject
  public CompositeDisposable compositeDisposable;
  @Inject
  public LinearLayoutManager llm;
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
    }
    observer.startWatching();
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
    fillAdapter();
  }

  private void fillAdapter() {
    //ArrayList<RecordingItem> recordingItems = new ArrayList<>();
    compositeDisposable.add(recordItemDataSource.getAllRecordings()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((recordingItem) -> {
          if (recordingItem.size() > 0) {
            mPlayListAdapter.addAllAndNotify((ArrayList<RecordingItem>) recordingItem);
          } else {
            emptyListLabel.setVisibility(View.VISIBLE);
            mRecordingsListView.setVisibility(View.GONE);
          }
        }));
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
    compositeDisposable.dispose();
  }
}




