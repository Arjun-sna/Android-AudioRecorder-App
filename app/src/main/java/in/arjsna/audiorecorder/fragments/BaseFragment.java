package in.arjsna.audiorecorder.fragments;

import android.content.Context;
import in.arjsna.audiorecorder.activities.BaseActivity;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.theme.ThemedFragment;

public abstract class BaseFragment extends ThemedFragment {
  BaseActivity mActivity;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof BaseActivity) {
      this.mActivity = (BaseActivity) context;
    }
  }

  public ActivityComponent getActivityComponent() {
    return mActivity.getActivityComponent();
  }
}
