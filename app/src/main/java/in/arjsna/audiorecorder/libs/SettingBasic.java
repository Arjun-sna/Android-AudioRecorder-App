package in.arjsna.audiorecorder.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import in.arjsna.audiorecorder.theme.Themed;

public class SettingBasic extends FrameLayout implements Themed {
  @StringRes private final int titleRes;
  @StringRes private final int captionRes;
  private TextView title;
  private TextView caption;

  public SettingBasic(Context context) {
    this(context, null);
  }

  public SettingBasic(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SettingBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setBackgroundResource(R.drawable.ripple);

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View rootView = inflater.inflate(R.layout.view_setting_basic, this);
    initViews(rootView);
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SettingBasic);
    titleRes = a.getResourceId(R.styleable.SettingBasic_settingTitle, 0);
    captionRes = a.getResourceId(R.styleable.SettingBasic_settingCaption, 0);
    int minimumApi = a.getInteger(R.styleable.SettingBasic_settingMinApi, 0);
    a.recycle();

    if (Build.VERSION.SDK_INT < minimumApi) setVisibility(GONE);
  }

  private void initViews(View rootView) {
    title = rootView.findViewById(R.id.title);
    caption = rootView.findViewById(R.id.caption);
  }

  @Override
  protected void onFinishInflate() {
    title.setText(titleRes);
    caption.setText(captionRes);
    super.onFinishInflate();
  }

  @Override
  public void refreshTheme(ThemeHelper themeHelper) {
    title.setTextColor(themeHelper.getTextColor());
    caption.setTextColor(themeHelper.getTextColor());
  }
}
