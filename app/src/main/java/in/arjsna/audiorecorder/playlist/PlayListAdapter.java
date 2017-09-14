package in.arjsna.audiorecorder.playlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.RecordingsViewHolder> {

  private static final String LOG_TAG = "PlayListAdapter";

  private final LayoutInflater inflater;

  private final Context mContext;
  private final ArrayList<RecordingItem> recordingItems;
  private PlayListFragment listItemEventsListener;

  public PlayListAdapter(Context context, RecordItemDataSource recordItemDataSource,
      ArrayList<RecordingItem> recordingItems) {
    mContext = context;
    this.recordingItems = recordingItems;
    inflater = LayoutInflater.from(mContext);
  }

  @Override public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

    RecordingItem currentRecording = recordingItems.get(position);
    long itemDuration = currentRecording.getLength();
    long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
    long seconds =
        TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);

    holder.vName.setText(currentRecording.getName());
    holder.vLength.setText(
        String.format(mContext.getString(R.string.play_time_format), minutes, seconds));
    holder.vDateAdded.setText(DateUtils.formatDateTime(mContext, currentRecording.getTime(),
        DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_NUMERIC_DATE
            | DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_YEAR));

    holder.cardView.setOnClickListener(view -> {
      listItemEventsListener.onItemClick(position, currentRecording);
    });

    holder.cardView.setOnLongClickListener(v -> {
      listItemEventsListener.onItemLongClick(position, currentRecording);
      return false;
    });
  }

  @Override public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = inflater.
        inflate(R.layout.card_view, parent, false);
    return new RecordingsViewHolder(itemView);
  }

  void addAllAndNotify(ArrayList<RecordingItem> recordingItems) {
    this.recordingItems.addAll(recordingItems);
    notifyDataSetChanged();
  }

  void setListItemEventsListener(PlayListFragment listItemEventsListener) {
    this.listItemEventsListener = listItemEventsListener;
  }

  void removeItemAndNotify(int position) {
    recordingItems.remove(position);
    notifyItemRemoved(position);
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

  //TODO
  public void removeOutOfApp(String filePath) {
    //user deletes a saved recording out of the application through another application
  }
}
