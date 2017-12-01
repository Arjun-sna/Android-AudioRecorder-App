package in.arjsna.audiorecorder;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import com.orhanobut.hawk.Hawk;
import com.squareup.leakcanary.LeakCanary;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import in.arjsna.audiorecorder.di.ApplicationComponent;
import in.arjsna.audiorecorder.di.DaggerApplicationComponent;
import javax.inject.Inject;

public class AudioRecorderApp extends Application implements HasActivityInjector, HasServiceInjector{
  private ApplicationComponent applicationComponent;

  @Inject DispatchingAndroidInjector<Activity> dispatchingAndroidActivityInjector;
  @Inject DispatchingAndroidInjector<Service> dispatchingAndroidServiceInjector;

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    LeakCanary.install(this);
    Hawk.init(getApplicationContext()).build();
    DaggerApplicationComponent.builder().application(this).build().inject(this);
  }

  @Override public AndroidInjector<Activity> activityInjector() {
    return dispatchingAndroidActivityInjector;
  }

  @Override public AndroidInjector<Service> serviceInjector() {
    return dispatchingAndroidServiceInjector;
  }
}
