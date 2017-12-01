package in.arjsna.audiorecorder.mvpbase;

import android.content.Context;
import dagger.android.support.AndroidSupportInjection;
import in.arjsna.audiorecorder.theme.ThemedFragment;

public abstract class BaseFragment extends ThemedFragment implements IMVPView {

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    AndroidSupportInjection.inject(this);
  }
}
