package in.arjsna.audiorecorder.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class ThemedFab extends FloatingActionButton implements Themed {

  public ThemedFab(Context context) {
    super(context);
  }

  public ThemedFab(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ThemedFab(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void refreshTheme(ThemeHelper theme) {
    setBackgroundTintList(ColorStateList.valueOf(theme.getAccentColor()));
  }
}
