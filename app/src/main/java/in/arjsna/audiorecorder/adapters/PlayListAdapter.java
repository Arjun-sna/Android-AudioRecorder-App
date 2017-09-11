package in.arjsna.audiorecorder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.fragments.PlaybackFragment;
import in.arjsna.audiorecorder.recording.Constants;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.RecordingsViewHolder> {

  private static final String LOG_TAG = "PlayListAdapter";

  private final RecordItemDataSource recordItemDataSource;
  private final LayoutInflater inflater;

  private final Context mContext;
  private final ArrayList<RecordingItem> recordingItems;

  public PlayListAdapter(Context context, RecordItemDataSource recordItemDataSource,
      ArrayList<RecordingItem> recordingItems) {
    mContext = context;
    this.recordItemDataSource = recordItemDataSource;
    this.recordingItems = recordingItems;
    inflater = LayoutInflater.from(mContext);
  }

  @Override public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

    RecordingItem item = recordingItems.get(position);
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

    holder.cardView.setOnClickListener(view -> {
      try {
        PlaybackFragment playbackFragment =
            new PlaybackFragment().newInstance(recordingItems.get(position));
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
      builder.setItems(items, (dialog, listItem) -> {
        if (listItem == 0) {
          shareFileDialog(holder.getAdapterPosition());
        }
        if (listItem == 1) {
          renameFileDialog(holder.getAdapterPosition());
        } else if (listItem == 2) {
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
    View itemView = inflater.
        inflate(R.layout.card_view, parent, false);
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
      vName = v.findViewById(R.id.file_name_text);
      vLength = v.findViewById(R.id.file_length_text);
      vDateAdded = v.findViewById(R.id.file_date_added_text);
      cardView = v.findViewById(R.id.card_view);
    }
  }

  @Override public int getItemCount() {
    return recordingItems.size();
  }

  private Single<String> remove(int position) {
    return Single.create((SingleOnSubscribe<String>) e -> {
      RecordingItem recordingItem = recordingItems.get(position);
      File file = new File(recordingItem.getFilePath());
      if (file.delete()) {
        recordItemDataSource.deleteRecordItem(recordingItem);
        recordingItems.remove(position);
        e.onSuccess(recordingItem.getName());
      } else {
        e.onError(new Exception("File deletion failed"));
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  //TODO
  public void removeOutOfApp(String filePath) {
    //user deletes a saved recording out of the application through another application
  }

  private Single<Integer> rename(int position, String name) {
    return Single.create((SingleOnSubscribe<Integer>) e -> {
      String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
      mFilePath += "/SoundRecorder/" + name;
      File f = new File(mFilePath);

      if (f.exists() && !f.isDirectory()) {
        e.onError(new Exception(mContext.getString(R.string.toast_file_exists)));
      } else {
        RecordingItem currentItem = recordingItems.get(position);
        File oldFilePath = new File(currentItem.getFilePath());
        oldFilePath.renameTo(f);
        currentItem.setName(name);
        recordItemDataSource.updateRecordItem(currentItem);
        e.onSuccess(position);
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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

    View view = inflater.inflate(R.layout.dialog_rename_file, null);

    final EditText input = (EditText) view.findViewById(R.id.new_name);

    renameFileBuilder.setTitle(mContext.getString(R.string.dialog_title_rename));
    renameFileBuilder.setCancelable(true);
    renameFileBuilder.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
        (dialog, id) -> {
          String value = input.getText().toString().trim() + Constants.AUDIO_RECORDER_FILE_EXT_WAV;
          rename(position, value).subscribe(new DisposableSingleObserver<Integer>() {
            @Override public void onSuccess(Integer removedPosition) {
              notifyItemChanged(removedPosition);
            }

            @Override public void onError(Throwable e) {
              Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
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
            remove(position).subscribe(new DisposableSingleObserver<String>() {
              @Override public void onSuccess(String removedFileName) {
                Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_delete), removedFileName),
                    Toast.LENGTH_SHORT).show();
                notifyItemRemoved(position);
              }

              @Override public void onError(Throwable e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
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
