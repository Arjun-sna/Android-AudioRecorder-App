package in.arjsna.audiorecorder.mvpbase;

import android.content.Context;
import in.arjsna.audiorecorder.di.components.ActivityComponent;
import in.arjsna.audiorecorder.theme.ThemedFragment;

public abstract class BaseFragment extends ThemedFragment implements IMVPView {
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
