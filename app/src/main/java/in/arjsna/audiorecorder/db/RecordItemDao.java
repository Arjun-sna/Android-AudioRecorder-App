package in.arjsna.audiorecorder.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import io.reactivex.Flowable;

@Dao public interface RecordItemDao {
  @Query("Select * from recordings") Flowable<RecordingItem> getAllRecordings();

  @Insert void insertNewRecordItem(RecordingItem recordingItem);

  @Update void updateRecordItem(RecordingItem recordingItem);

  @Delete void deleteRecordItem(RecordingItem recordingItem);

  @Query("Select count() from recordings") int getCount();
}
