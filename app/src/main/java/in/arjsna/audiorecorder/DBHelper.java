package in.arjsna.audiorecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.listeners.OnDatabaseChangedListener;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Comparator;

public class DBHelper extends SQLiteOpenHelper {

  private static final String LOG_TAG = "DBHelper";

  private OnDatabaseChangedListener mOnDatabaseChangedListener;

  private static final String DATABASE_NAME = "saved_recordings.db";
  private static final int DATABASE_VERSION = 1;

  static abstract class DBHelperItem implements BaseColumns {
    static final String TABLE_NAME = "saved_recordings";

    static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
    static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
    static final String COLUMN_NAME_RECORDING_LENGTH = "length";
    static final String COLUMN_NAME_TIME_ADDED = "time_added";
  }

  private static final String TEXT_TYPE = " TEXT";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
      + DBHelperItem.TABLE_NAME
      + " ("
      + DBHelperItem._ID
      + " INTEGER PRIMARY KEY"
      + COMMA_SEP
      + DBHelperItem.COLUMN_NAME_RECORDING_NAME
      + TEXT_TYPE
      + COMMA_SEP
      + DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH
      + TEXT_TYPE
      + COMMA_SEP
      + DBHelperItem.COLUMN_NAME_RECORDING_LENGTH
      + " INTEGER "
      + COMMA_SEP
      + DBHelperItem.COLUMN_NAME_TIME_ADDED
      + " INTEGER "
      + ")";

  @SuppressWarnings("unused") private static final String SQL_DELETE_ENTRIES =
      "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
    mOnDatabaseChangedListener = listener;
  }

  public Observable<ArrayList<RecordingItem>> getAllRecordings() {
    return Observable.fromCallable(() -> {
      SQLiteDatabase db = getReadableDatabase();
      ArrayList<RecordingItem> recordingItems = new ArrayList<>();
      String[] projection = {
          DBHelperItem._ID, DBHelperItem.COLUMN_NAME_RECORDING_NAME,
          DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,
          DBHelperItem.COLUMN_NAME_TIME_ADDED
      };
      Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
      while (c.moveToNext()) {
        RecordingItem item = new RecordingItem();
        item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
        item.setName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_NAME)));
        item.setFilePath(
            c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH)));
        item.setLength(c.getInt(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH)));
        item.setTime(c.getLong(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME_ADDED)));
        recordingItems.add(item);
      }
      c.close();
      return recordingItems;
    }).subscribeOn(Schedulers.io());
  }

  public void removeItemWithId(int id) {
    SQLiteDatabase db = getWritableDatabase();
    String[] whereArgs = {String.valueOf(id)};
    db.delete(DBHelperItem.TABLE_NAME, "_ID=?", whereArgs);
  }

  public int getCount() {
    SQLiteDatabase db = getReadableDatabase();
    String[] projection = {DBHelperItem._ID};
    Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
    int count = c.getCount();
    c.close();
    return count;
  }

  public class RecordingComparator implements Comparator<RecordingItem> {
    public int compare(RecordingItem item1, RecordingItem item2) {
      Long o1 = item1.getTime();
      Long o2 = item2.getTime();
      return o2.compareTo(o1);
    }
  }

  public long addRecording(String recordingName, String filePath, long length) {

    SQLiteDatabase db = getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, length);
    cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
    long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);

    if (mOnDatabaseChangedListener != null) {
      mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
    }

    return rowId;
  }

  public void renameItem(RecordingItem item, String recordingName, String filePath) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
    db.update(DBHelperItem.TABLE_NAME, cv, DBHelperItem._ID + "=" + item.getId(), null);

    if (mOnDatabaseChangedListener != null) {
      mOnDatabaseChangedListener.onDatabaseEntryRenamed();
    }
  }

  public long restoreRecording(RecordingItem item) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, item.getName());
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, item.getFilePath());
    cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, item.getLength());
    cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, item.getTime());
    cv.put(DBHelperItem._ID, item.getId());
    long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);
    if (mOnDatabaseChangedListener != null) {
      //mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
    }
    return rowId;
  }
}
