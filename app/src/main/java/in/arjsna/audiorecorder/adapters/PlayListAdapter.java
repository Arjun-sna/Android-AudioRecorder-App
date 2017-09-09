package in.arjsna.audiorecorder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import in.arjsna.audiorecorder.DBHelper;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.RecordingItem;
import in.arjsna.audiorecorder.fragments.PlaybackFragment;
import in.arjsna.audiorecorder.listeners.OnDatabaseChangedListener;
import in.arjsna.audiorecorder.recording.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.RecordingsViewHolder>
    implements OnDatabaseChangedListener {

  private static final String LOG_TAG = "PlayListAdapter";

  private final DBHelper mDatabase;

  private RecordingItem item;
  private Context mContext;
  private final LinearLayoutManager llm;
  private ArrayList<RecordingItem> recordingItems;

  public PlayListAdapter(Context context, LinearLayoutManager linearLayoutManager,
      ArrayList<RecordingItem> recordingItems) {
    super();
    mContext = context;
    mDatabase = new DBHelper(mContext);
    mDatabase.setOnDatabaseChangedListener(this);
    this.recordingItems = recordingItems;
    llm = linearLayoutManager;
  }

  @Override public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

    item = recordingItems.get(position);
    long itemDuration = item.getLength();

    long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
    long seconds =
        TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);

    holder.vName.setText(item.getName());
    holder.vLength.setText(
        String.format(mContext.getString(R.string.play_time_format), minutes, seconds));
    holder.vDateAdded.setText(DateUtils.formatDateTime(mContext, item.getTime(),
        DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_NUMERIC_DATE
            | DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_YEAR));

    // define an on click listener to open PlaybackFragment
    holder.cardView.setOnClickListener(view -> {
      try {
        PlaybackFragment playbackFragment =
            new PlaybackFragment().newInstance(recordingItems.get(holder.getAdapterPosition()));

        FragmentTransaction transaction =
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();

        playbackFragment.show(transaction, "dialog_playback");
      } catch (Exception e) {
        Log.e(LOG_TAG, "exception", e);
      }
    });

    holder.cardView.setOnLongClickListener(v -> {

      ArrayList<String> entrys = new ArrayList<>();
      entrys.add(mContext.getString(R.string.dialog_file_share));
      entrys.add(mContext.getString(R.string.dialog_file_rename));
      entrys.add(mContext.getString(R.string.dialog_file_delete));

      final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

      // File delete confirm
      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
      builder.setTitle(mContext.getString(R.string.dialog_title_options));
      builder.setItems(items, (dialog, item) -> {
        if (item == 0) {
          shareFileDialog(holder.getAdapterPosition());
        }
        if (item == 1) {
          renameFileDialog(holder.getAdapterPosition());
        } else if (item == 2) {
          deleteFileDialog(holder.getAdapterPosition());
        }
      });
      builder.setCancelable(true);
      builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
          (dialog, id) -> dialog.cancel());

      AlertDialog alert = builder.create();
      alert.show();

      return false;
    });
  }

  @Override public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = LayoutInflater.
        from(parent.getContext()).
        inflate(R.layout.card_view, parent, false);

    mContext = parent.getContext();

    return new RecordingsViewHolder(itemView);
  }

  public void addAllAndNotify(ArrayList<RecordingItem> recordingItems) {
    this.recordingItems.addAll(recordingItems);
    notifyDataSetChanged();
  }

  static class RecordingsViewHolder extends RecyclerView.ViewHolder {
    final TextView vName;
    final TextView vLength;
    final TextView vDateAdded;
    final View cardView;

    RecordingsViewHolder(View v) {
      super(v);
      vName = (TextView) v.findViewById(R.id.file_name_text);
      vLength = (TextView) v.findViewById(R.id.file_length_text);
      vDateAdded = (TextView) v.findViewById(R.id.file_date_added_text);
      cardView = v.findViewById(R.id.card_view);
    }
  }

  @Override public int getItemCount() {
    return recordingItems.size();
  }

  @Override public void onNewDatabaseEntryAdded() {
    //item added to top of the list
    notifyItemInserted(getItemCount() - 1);
    llm.scrollToPosition(getItemCount() - 1);
  }

  @Override
  //TODO
  public void onDatabaseEntryRenamed() {

  }

  private void remove(int position) {
    //remove item from database, recyclerview and storage

    //delete file from storage
    File file = new File(recordingItems.get(position).getFilePath());
    file.delete();

    Toast.makeText(mContext,
        String.format(mContext.getString(R.string.toast_file_delete), recordingItems.get(position).getName()),
        Toast.LENGTH_SHORT).show();

    mDatabase.removeItemWithId(recordingItems.get(position).getId());
    recordingItems.remove(position);
    notifyItemRemoved(position);
  }

  //TODO
  public void removeOutOfApp(String filePath) {
    //user deletes a saved recording out of the application through another application
  }

  private void rename(int position, String name) {
    //rename a file

    String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    mFilePath += "/SoundRecorder/" + name;
    File f = new File(mFilePath);

    if (f.exists() && !f.isDirectory()) {
      //file name is not unique, cannot rename file.
      Toast.makeText(mContext, String.format(mContext.getString(R.string.toast_file_exists), name),
          Toast.LENGTH_SHORT).show();
    } else {
      //file name is unique, rename file
      File oldFilePath = new File(recordingItems.get(position).getFilePath());
      oldFilePath.renameTo(f);
      mDatabase.renameItem(recordingItems.get(position), name, mFilePath);
      notifyItemChanged(position);
    }
  }

  private void shareFileDialog(int position) {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM,
        Uri.fromFile(new File(recordingItems.get(position).getFilePath())));
    shareIntent.setType("audio/mp4");
    mContext.startActivity(Intent.createChooser(shareIntent, mContext.getText(R.string.send_to)));
  }

  private void renameFileDialog(final int position) {
    // File rename dialog
    AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

    LayoutInflater inflater = LayoutInflater.from(mContext);
    View view = inflater.inflate(R.layout.dialog_rename_file, null);

    final EditText input = (EditText) view.findViewById(R.id.new_name);

    renameFileBuilder.setTitle(mContext.getString(R.string.dialog_title_rename));
    renameFileBuilder.setCancelable(true);
    renameFileBuilder.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
        (dialog, id) -> {
          try {
            String value =
                input.getText().toString().trim() + Constants.AUDIO_RECORDER_FILE_EXT_WAV;
            rename(position, value);
          } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
          }

          dialog.cancel();
        });
    renameFileBuilder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
        (dialog, id) -> dialog.cancel());

    renameFileBuilder.setView(view);
    AlertDialog alert = renameFileBuilder.create();
    alert.show();
  }

  private void deleteFileDialog(final int position) {
    // File delete confirm
    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mContext);
    confirmDelete.setTitle(mContext.getString(R.string.dialog_title_delete));
    confirmDelete.setMessage(mContext.getString(R.string.dialog_text_delete));
    confirmDelete.setCancelable(true);
    confirmDelete.setPositiveButton(mContext.getString(R.string.dialog_action_yes),
        (dialog, id) -> {
          try {
            //remove item from database, recyclerview, and storage
            remove(position);
          } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
          }

          dialog.cancel();
        });
    confirmDelete.setNegativeButton(mContext.getString(R.string.dialog_action_no),
        (dialog, id) -> dialog.cancel());

    AlertDialog alert = confirmDelete.create();
    alert.show();
  }
}
