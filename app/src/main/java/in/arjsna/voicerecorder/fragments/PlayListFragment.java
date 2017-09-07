package in.arjsna.voicerecorder.fragments;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.arjsna.voicerecorder.R;
import in.arjsna.voicerecorder.adapters.RecordListAdapter;

public class PlayListFragment extends Fragment {
  private static final String LOG_TAG = "PlayListFragment";

  private RecordListAdapter mRecordListAdapter;

  public static PlayListFragment newInstance() {
    return new PlayListFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    observer.startWatching();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

    RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
    llm.setOrientation(LinearLayoutManager.VERTICAL);

    //newest to oldest order (database stores from oldest to newest)
    llm.setReverseLayout(true);
    llm.setStackFromEnd(true);

    mRecyclerView.setLayoutManager(llm);
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    mRecordListAdapter = new RecordListAdapter(getActivity(), llm);
    mRecyclerView.setAdapter(mRecordListAdapter);

    return v;
  }

  FileObserver observer = new FileObserver(
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
        mRecordListAdapter.removeOutOfApp(filePath);
      }
    }
  };
}




