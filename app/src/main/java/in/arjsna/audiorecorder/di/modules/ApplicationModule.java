package in.arjsna.audiorecorder.di.modules;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.db.AppDataBase;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Module
public class ApplicationModule {
  private Application application;

  public ApplicationModule(Application application) {
    this.application = application;
  }

  @Provides
  @ApplicationContext
  @Singleton
  Context provideApplicationContext() {
    return application.getApplicationContext();
  }

  @Provides
  @Singleton
  RecordItemDataSource provideRecordItemDataSource(@ApplicationContext Context context) {
    return AppDataBase.getInstance(context).getRecordItemDataSource();
  }
}
