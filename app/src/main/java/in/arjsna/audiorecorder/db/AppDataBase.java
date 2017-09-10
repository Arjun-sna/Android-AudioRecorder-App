package in.arjsna.audiorecorder.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = { RecordingItem.class }, version = 1)
public abstract class AppDataBase extends RoomDatabase {
  public abstract RecordItemDao recordItemDao();

  private static final String DATABASE_NAME = "saved_recordings.db";

  private static AppDataBase appDataBaseInstance;

  public static AppDataBase getInstance(Context context) {
    if (appDataBaseInstance == null) {
      appDataBaseInstance = Room.databaseBuilder(context, AppDataBase.class, DATABASE_NAME).build();
    }
    return appDataBaseInstance;
  }
}
