package in.arjsna.audiorecorder.playlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.fragments.PlaybackFragment;
import in.arjsna.audiorecorder.mvpbase.BaseFragment;
import in.arjsna.audiorecorder.recording.Constants;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import java.io.File;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListFragment extends BaseFragment
    implements PlayListMVPView, ListItemEventsListener {
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
    mPlayListAdapter.setListItemEventsListener(this);
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

  @Override public void notifyListItemChange(Integer position) {
    mPlayListAdapter.notifyItemChanged(position);
  }

  @Override public void showError(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
  }

  @Override public void onItemLongClick(int position, RecordingItem recordingItem) {
    ArrayList<String> fileOptions = new ArrayList<>();
    fileOptions.add(getActivity().getString(R.string.dialog_file_share));
    fileOptions.add(getActivity().getString(R.string.dialog_file_rename));
    fileOptions.add(getActivity().getString(R.string.dialog_file_delete));

    final CharSequence[] items = fileOptions.toArray(new CharSequence[fileOptions.size()]);

    // File delete confirm
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getString(R.string.dialog_title_options));
    builder.setItems(items, (dialog, listItem) -> {
      switch (listItem) {
        case 0:
          shareFileDialog(recordingItem);
          break;
        case 1:
          renameFileDialog(recordingItem, position);
          break;
        case 2:
          deleteFileDialog(recordingItem);
          break;
      }
    });
    builder.setCancelable(true);
    builder.setNegativeButton(getString(R.string.dialog_action_cancel),
        (dialog, id) -> dialog.cancel());

    AlertDialog alert = builder.create();
    alert.show();
  }

  private void shareFileDialog(RecordingItem recordingItem) {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM,
        Uri.fromFile(new File(recordingItem.getFilePath())));
    shareIntent.setType("audio/mp4");
    getActivity().startActivity(Intent.createChooser(shareIntent, getText(R.string.send_to)));
  }

  private void renameFileDialog(final RecordingItem recordingItem, int position) {
    AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(getActivity());
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rename_file, null);
    final EditText input = view.findViewById(R.id.new_name);
    renameFileBuilder.setTitle(getString(R.string.dialog_title_rename));
    renameFileBuilder.setCancelable(true);
    renameFileBuilder.setPositiveButton(getString(R.string.dialog_action_ok),
        (dialog, id) -> {
          String value = input.getText().toString().trim() + Constants.AUDIO_RECORDER_FILE_EXT_WAV;
          playListPresenter.renameFile(recordingItem, position, value);
          dialog.cancel();
        });
    renameFileBuilder.setNegativeButton(getActivity().getString(R.string.dialog_action_cancel),
        (dialog, id) -> dialog.cancel());
    renameFileBuilder.setView(view);
    AlertDialog alert = renameFileBuilder.create();
    alert.show();
  }

  private void deleteFileDialog(final RecordingItem recordingItem) {
    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getActivity());
    confirmDelete.setTitle(getString(R.string.dialog_title_delete));
    confirmDelete.setMessage(getString(R.string.dialog_text_delete));
    confirmDelete.setCancelable(true);
    confirmDelete.setPositiveButton(getString(R.string.dialog_action_yes),
        (dialog, id) -> {
          playListPresenter.deleteFile(recordingItem);
          dialog.cancel();
        });
    confirmDelete.setNegativeButton(getString(R.string.dialog_action_no),
        (dialog, id) -> dialog.cancel());
    AlertDialog alert = confirmDelete.create();
    alert.show();
  }

  @Override public void onItemClick(int position, RecordingItem recordingItem) {
    PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(recordingItem);
    playbackFragment.show(getActivity().getSupportFragmentManager(), "dialog_playback");
  }
}




