package in.arjsna.audiorecorder.di.modules;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import in.arjsna.audiorecorder.di.ActivityContext;
import io.reactivex.disposables.CompositeDisposable;

@Module public class ActivityModule {
  private AppCompatActivity appCompatActivity;

  public ActivityModule(AppCompatActivity appCompatActivity) {
    this.appCompatActivity = appCompatActivity;
  }

  @Provides @ActivityContext Context provideActivityContext() {
    return appCompatActivity;
  }

  @Provides CompositeDisposable provideCompositeDisposable() {
    return new CompositeDisposable();
  }

  @Provides LinearLayoutManager provideLinearLayoutManager(@ActivityContext Context context) {
    return new LinearLayoutManager(context);
  }
}
