package in.arjsna.audiorecorder.db;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class RecordItemDataSource {
  private RecordItemDao recordItemDao;

  public RecordItemDataSource(RecordItemDao recordItemDao) {
    this.recordItemDao = recordItemDao;
  }

  public Single<List<RecordingItem>> getAllRecordings() {
    return Single.fromCallable(() -> recordItemDao.getAllRecordings()).subscribeOn(Schedulers.io());
  }

  public Single<Boolean> insertNewRecordItemAsync(RecordingItem recordingItem) {
    return Single.fromCallable(() -> recordItemDao.insertNewRecordItem(recordingItem) > 1)
        .subscribeOn(Schedulers.io());
  }

  public long insertNewRecordItem(RecordingItem recordingItem) {
    return recordItemDao.insertNewRecordItem(recordingItem);
  }

  public Single<Boolean> deleteRecordItemAsync(RecordingItem recordingItem) {
    return Single.fromCallable(() -> recordItemDao.deleteRecordItem(recordingItem) > 1)
        .subscribeOn(Schedulers.io());
  }

  public int deleteRecordItem(RecordingItem recordingItem) {
    return recordItemDao.deleteRecordItem(recordingItem);
  }

  public Single<Boolean> updateRecordItemAsync(RecordingItem recordingItem) {
    return Single.fromCallable(() -> recordItemDao.updateRecordItem(recordingItem) > 1)
        .subscribeOn(Schedulers.io());
  }

  public int updateRecordItem(RecordingItem recordingItem) {
    return recordItemDao.updateRecordItem(recordingItem);
  }

  public int getRecordingsCount() {
    return recordItemDao.getCount();
  }
}
