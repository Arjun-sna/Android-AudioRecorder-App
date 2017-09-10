package in.arjsna.audiorecorder;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.db.AppDataBase;

public class AudioRecorderApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Hawk.init(getApplicationContext()).build();
    RoomDatabase.Builder<AppDataBase> appDataBaseInstance =
        Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "dd");

  }
}
