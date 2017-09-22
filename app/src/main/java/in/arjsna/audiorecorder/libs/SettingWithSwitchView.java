package in.arjsna.audiorecorder.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.theme.ThemeHelper;
import in.arjsna.audiorecorder.theme.Themed;
import in.arjsna.audiorecorder.theme.ThemedActivity;

public class SettingWithSwitchView extends FrameLayout implements View.OnClickListener, Themed {
  private final String preferenceKey;
  @StringRes private final int titleRes;
  @StringRes private final int captionRes;
  private final boolean defaultValue;
  private TextView title;
  private TextView caption;
  private SwitchCompat toggle;
  @Nullable private OnClickListener clickListener;

  public SettingWithSwitchView(Context context) {
    this(context, null);
  }

  public SettingWithSwitchView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SettingWithSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setBackgroundResource(R.drawable.ripple);

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View rootView = inflater.inflate(R.layout.view_setting_switch, this);
    initViews(rootView);
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SettingWithSwitchView);
    final int prefKeyRes =
        a.getResourceId(R.styleable.SettingWithSwitchView_settingPreferenceKey, 0);
    if (prefKeyRes == 0) throw new IllegalArgumentException("Invalid preference reference");
    preferenceKey = getResources().getString(prefKeyRes);
    titleRes = a.getResourceId(R.styleable.SettingWithSwitchView_settingTitle, 0);
    captionRes = a.getResourceId(R.styleable.SettingWithSwitchView_settingCaption, 0);
    defaultValue = a.getBoolean(R.styleable.SettingWithSwitchView_settingDefaultValue, false);
    int minimumApi = 0;//a.getInteger(R.styleable.SettingWithSwitchView_settingMinApi, 0);
    a.recycle();

    if (Build.VERSION.SDK_INT < minimumApi) setVisibility(GONE);
  }

  private void initViews(View rootView) {
    title = rootView.findViewById(R.id.title);
    caption = rootView.findViewById(R.id.caption);
    toggle = rootView.findViewById(R.id.toggle);
  }

  @Override
  protected void onFinishInflate() {
    title.setText(titleRes);
    caption.setText(captionRes);
    toggle.setChecked(isChecked());
    super.setOnClickListener(this);
    super.onFinishInflate();
  }

  @Override
  public void setOnClickListener(@Nullable OnClickListener clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (!isInEditMode()) refreshTheme(((ThemedActivity) getContext()).getThemeHelper());
  }

  @Override
  public void onClick(View view) {
    toggle();
    refreshTheme(((ThemedActivity) getContext()).getThemeHelper());
    if (clickListener != null) clickListener.onClick(this);
  }

  @Override
  public void refreshTheme(ThemeHelper themeHelper) {
    themeHelper.setSwitchCompactColor(toggle, themeHelper.getAccentColor());
    title.setTextColor(themeHelper.getTextColor());
    caption.setTextColor(themeHelper.getTextColor());
  }

  public boolean isChecked() {
    return Hawk.get(preferenceKey, defaultValue);
  }

  public boolean toggle() {
    Hawk.put(preferenceKey, !isChecked());
    boolean checked = isChecked();
    toggle.setChecked(checked);
    return checked;
  }
}
