package in.arjsna.audiorecorder.playlist;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import in.arjsna.audiorecorder.mvpbase.BaseFragment;
import in.arjsna.audiorecorder.recordingservice.Constants;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListFragment extends BaseFragment implements PlayListMVPView {
  private static final String LOG_TAG = "PlayListFragment";

  @Inject
  public PlayListAdapter mPlayListAdapter;

  @Inject
  public PlayListPresenter<PlayListMVPView> playListPresenter;

  private RecyclerView mRecordingsListView;
  private TextView emptyListLabel;
  private MediaPlayer mMediaPlayer;

  public static PlayListFragment newInstance() {
    return new PlayListFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    playListPresenter.onAttach(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
    initViews(v);
    mMediaPlayer = new MediaPlayer();
    return v;
  }

  private void initViews(View v) {
    emptyListLabel = v.findViewById(R.id.empty_list_label);
    mRecordingsListView = v.findViewById(R.id.recyclerView);
    mRecordingsListView.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
    llm.setOrientation(LinearLayoutManager.VERTICAL);

    //newest to oldest order (database stores from oldest to newest)
    llm.setReverseLayout(true);
    llm.setStackFromEnd(true);

    mRecordingsListView.setLayoutManager(llm);
    //mRecordingsListView.setItemAnimator(new DefaultItemAnimator());
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
    playListPresenter.onDetach();
    super.onDestroy();
  }

  @Override public void notifyListAdapter() {
    mPlayListAdapter.notifyDataSetChanged();
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

  private int positionOfCurrentViewHolder = -1;
  private PlayListAdapter.RecordingsViewHolder recordingsViewHolder;

  Handler uiThreadHandler = new Handler();

  @Override public void updateProgressInListItem(Integer position) {
    if (position != positionOfCurrentViewHolder || recordingsViewHolder == null) {
      positionOfCurrentViewHolder = position;
      recordingsViewHolder =
          (PlayListAdapter.RecordingsViewHolder) mRecordingsListView.findViewHolderForAdapterPosition(
              position);
    }
    if (recordingsViewHolder != null && recordingsViewHolder.getAdapterPosition() == position) {
      uiThreadHandler.post(() -> recordingsViewHolder.updateProgressInSeekBar(position));
    } else {
      positionOfCurrentViewHolder = -1;
      recordingsViewHolder = null;
    }
  }

  @Override public void updateTimerInListItem(int position) {
    if (recordingsViewHolder != null) {
      uiThreadHandler.post(() -> recordingsViewHolder.updatePlayTimer(position));
    }
  }

  @Override public void notifyListItemChange(Integer position) {
    mPlayListAdapter.notifyItemChanged(position);
  }

  @Override public void showError(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
  }

  @Override public void notifyListItemRemove(Integer position) {
    mPlayListAdapter.notifyItemRemoved(position);
  }

  @Override public void showFileOptionDialog(int position, RecordingItem recordingItem) {
    ArrayList<String> fileOptions = new ArrayList<>();
    fileOptions.add(getString(R.string.dialog_file_share));
    fileOptions.add(getString(R.string.dialog_file_rename));
    fileOptions.add(getString(R.string.dialog_file_delete));

    final CharSequence[] items = fileOptions.toArray(new CharSequence[fileOptions.size()]);

    // File delete confirm
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getString(R.string.dialog_title_options));
    builder.setItems(items, (dialog, listItem) -> {
      switch (listItem) {
        case 0:
          playListPresenter.shareFileClicked(position);
          break;
        case 1:
          playListPresenter.renameFileClicked(position);
          break;
        case 2:
          playListPresenter.deleteFileClicked(position);
          break;
      }
    });
    builder.setCancelable(true);
    builder.setNegativeButton(getString(R.string.dialog_action_cancel),
        (dialog, id) -> dialog.cancel());

    AlertDialog alert = builder.create();
    alert.show();
  }

  @Override
  public void shareFileDialog(String filePath) {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM,
        Uri.fromFile(new File(filePath)));
    shareIntent.setType("audio/mp4");
    getActivity().startActivity(Intent.createChooser(shareIntent, getText(R.string.send_to)));
  }

  @Override
  public void showRenameFileDialog(int position) {
    AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(getActivity());
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rename_file, null);
    final EditText input = view.findViewById(R.id.new_name);
    renameFileBuilder.setTitle(getString(R.string.dialog_title_rename));
    renameFileBuilder.setCancelable(true);
    renameFileBuilder.setPositiveButton(getString(R.string.dialog_action_ok),
        (dialog, id) -> {
          String value = input.getText().toString().trim() + Constants.AUDIO_RECORDER_FILE_EXT_WAV;
          playListPresenter.renameFile(position, value);
          dialog.cancel();
        });
    renameFileBuilder.setNegativeButton(getActivity().getString(R.string.dialog_action_cancel),
        (dialog, id) -> dialog.cancel());
    renameFileBuilder.setView(view);
    AlertDialog alert = renameFileBuilder.create();
    alert.show();
  }

  @Override
  public void showDeleteFileDialog(int position) {
    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getActivity());
    confirmDelete.setTitle(getString(R.string.dialog_title_delete));
    confirmDelete.setMessage(getString(R.string.dialog_text_delete));
    confirmDelete.setCancelable(true);
    confirmDelete.setPositiveButton(getString(R.string.dialog_action_yes),
        (dialog, id) -> {
          playListPresenter.deleteFile(position);
          dialog.cancel();
        });
    confirmDelete.setNegativeButton(getString(R.string.dialog_action_no),
        (dialog, id) -> dialog.cancel());
    AlertDialog alert = confirmDelete.create();
    alert.show();
  }

  @Override public void pauseMediaPlayer(int position) {
    mMediaPlayer.pause();
  }

  @Override public void resumeMediaPlayer(int position) {
    mMediaPlayer.start();
  }

  @Override public void stopMediaPlayer(int currentPlayingItem) {
    if (mMediaPlayer != null) {
      Log.i("Debug ", "Stopping");
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  @Override public void startMediaPlayer(int position, RecordingItem recordingItem)
      throws IOException {
    mMediaPlayer = new MediaPlayer();
    mMediaPlayer.setDataSource(recordingItem.getFilePath());
    mMediaPlayer.prepare();
    mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
    mMediaPlayer.setOnCompletionListener(mp -> playListPresenter.mediaPlayerStopped());
    Log.i("Debug ", "Started");
  }
}




