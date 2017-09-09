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
import in.arjsna.audiorecorder.DBHelper;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.adapters.PlayListAdapter;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import in.arjsna.audiorecorder.theme.ThemedFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;

public class PlayListFragment extends ThemedFragment {
  private static final String LOG_TAG = "PlayListFragment";

  private PlayListAdapter mPlayListAdapter;
  private DBHelper dbHelper;
  private RecyclerView mRecordingsListView;
  private LinearLayoutManager llm;
  private TextView emptyListLabel;

  public static PlayListFragment newInstance() {
    return new PlayListFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    observer.startWatching();
    dbHelper = new DBHelper(getActivity());
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
    initViews(v);
    return v;
  }

  private void initViews(View v) {
    emptyListLabel = (TextView) v.findViewById(R.id.empty_list_label);
    mRecordingsListView = (RecyclerView) v.findViewById(R.id.recyclerView);
    mRecordingsListView.setHasFixedSize(true);
    llm = new LinearLayoutManager(getActivity());
    llm.setOrientation(LinearLayoutManager.VERTICAL);

    //newest to oldest order (database stores from oldest to newest)
    llm.setReverseLayout(true);
    llm.setStackFromEnd(true);

    mRecordingsListView.setLayoutManager(llm);
    mRecordingsListView.setItemAnimator(new DefaultItemAnimator());
    mPlayListAdapter = new PlayListAdapter(getActivity(), llm, new ArrayList<>());
    mRecordingsListView.setAdapter(mPlayListAdapter);
    fillAdapter();
  }

  private void fillAdapter() {
    dbHelper.getAllRecordings()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(recordingItems -> {
          if (recordingItems.size() > 0) {
            mPlayListAdapter.addAllAndNotify(recordingItems);
          } else {
            emptyListLabel.setVisibility(View.VISIBLE);
            mRecordingsListView.setVisibility(View.GONE);
          }
        });
  }

  final FileObserver observer = new FileObserver(
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
}




