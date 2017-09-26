package in.arjsna.audiorecorder.playlist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.di.qualifiers.ActivityContext;
import in.arjsna.audiorecorder.libs.FillSeekBar;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.RecordingsViewHolder> {

  private static final String LOG_TAG = "PlayListAdapter";

  private final LayoutInflater inflater;

  private final Context mContext;
  private final PlayListPresenter<PlayListMVPView> playListPresenter;
  //private final ArrayList<RecordingItem> recordingItems;

  @Inject
  public PlayListAdapter(@ActivityContext AppCompatActivity context,
      PlayListPresenter<PlayListMVPView> playListPresenter) {
    mContext = context;
    //this.recordingItems = recordingItems;
    this.playListPresenter = playListPresenter;
    inflater = LayoutInflater.from(mContext);
  }

  @Override public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

    RecordingItem currentRecording = playListPresenter.getListItemAt(position);
    long itemDuration = currentRecording.getLength();
    long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
    long seconds =
        TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);
    holder.fillSeekBar.setMaxVal(itemDuration);
    holder.vName.setText(currentRecording.getName());
    holder.vLength.setText(
        String.format(mContext.getString(R.string.play_time_format), minutes, seconds));
    holder.vDateAdded.setText(DateUtils.formatDateTime(mContext, currentRecording.getTime(),
        DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_NUMERIC_DATE
            | DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_YEAR));

    holder.cardView.setOnClickListener(
        view -> playListPresenter.onListItemClick(holder.getAdapterPosition()));

    holder.cardView.setOnLongClickListener(v -> {
      playListPresenter.onListItemLongClick(holder.getAdapterPosition());
      return false;
    });

    if (currentRecording.isPlaying) {
      if (currentRecording.isPaused) {
        holder.fillSeekBar.pauseProgress();
      } else {
        holder.fillSeekBar.startProgress();
      }
    } else {
      holder.fillSeekBar.stopProgress();
    }
  }

  //public void startProgress(int position) {
  //  recordingItems.get(position).isPlaying = true;
  //  notifyItemChanged(position);
  //}
  //
  //public void stopProgress(int currentPlayingItem) {
  //  recordingItems.get(currentPlayingItem).isPlaying = false;
  //  notifyItemChanged(currentPlayingItem);
  //}
  //
  //public void pauseProgress(int currentPlayingItem) {
  //  recordingItems.get(currentPlayingItem).isPaused = true;
  //  notifyItemChanged(currentPlayingItem);
  //}
  //
  //public void resumeProgress(int currentPlayingItem) {
  //  recordingItems.get(currentPlayingItem).isPaused = false;
  //  notifyItemChanged(currentPlayingItem);
  //}

  @Override public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RecordingsViewHolder(inflater.
        inflate(R.layout.record_list_item, parent, false));
  }

  //
  //void removeItemAndNotify(int position) {
  //  recordingItems.remove(position);
  //  notifyItemRemoved(position);
  //}

  static class RecordingsViewHolder extends RecyclerView.ViewHolder {
    final TextView vName;
    final TextView vLength;
    final TextView vDateAdded;
    final View cardView;
    final FillSeekBar fillSeekBar;

    RecordingsViewHolder(View v) {
      super(v);
      vName = v.findViewById(R.id.file_name_text);
      vLength = v.findViewById(R.id.file_length_text);
      vDateAdded = v.findViewById(R.id.file_date_added_text);
      cardView = v.findViewById(R.id.record_item_root_view);
      fillSeekBar = v.findViewById(R.id.attached_seek_bar);
    }
  }

  @Override public int getItemCount() {
    return playListPresenter.getListItemCount();
  }

  //TODO
  public void removeOutOfApp(String filePath) {
    //user deletes a saved recording out of the application through another application
  }
}
