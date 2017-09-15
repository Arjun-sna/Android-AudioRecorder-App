package in.arjsna.audiorecorder.theme;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.R;

public abstract class ThemedActivity extends AppCompatActivity implements UiElementInizializer {

  private ThemeHelper themeHelper;

  private boolean coloredNavBar;
  private boolean applyThemeSingleImgAct;
  private boolean customIconColor;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    themeHelper = ThemeHelper.getInstance(getApplicationContext());
  }

  @Override
  public void onResume() {
    super.onResume();
    updateTheme();
    updateUiElements();
    setStatusBarColor();
  }

  public ThemeHelper getThemeHelper() {
    return themeHelper;
  }

  public void updateTheme() {
    themeHelper.updateTheme();
    applyThemeSingleImgAct = Hawk.get("apply_theme_img_act", true);
  }

  @CallSuper
  @Override
  public void updateUiElements() {
    setStatusBarColor();
    for (View view : ViewUtil.getAllChildren(findViewById(android.R.id.content))) {
      if (view instanceof Themed) ((Themed) view).refreshTheme(getThemeHelper());
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) protected void setNavBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (isNavigationBarColored()) {
        getWindow().setNavigationBarColor(getPrimaryColor());
      } else {
        getWindow().setNavigationBarColor(
            ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  protected void setStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
    }
  }

  protected void setScrollViewColor(ScrollView scr) {
    themeHelper.setScrollViewColor(scr);
  }

  public boolean isNavigationBarColored() {
    return coloredNavBar;
  }

  public boolean themeOnSingleImgAct() {
    return applyThemeSingleImgAct;
  }

  public void setBaseTheme(Theme baseTheme) {
    themeHelper.setBaseTheme(baseTheme);
  }

  public void themeSeekBar(SeekBar bar) {
    themeHelper.themeSeekBar(bar);
  }

  public int getPrimaryColor() {
    return themeHelper.getPrimaryColor();
  }

  public int getAccentColor() {
    return themeHelper.getAccentColor();
  }

  public Theme getBaseTheme() {
    return themeHelper.getBaseTheme();
  }

  public int getBackgroundColor() {
    return themeHelper.getBackgroundColor();
  }

  protected int getInvertedBackgroundColor() {
    return themeHelper.getInvertedBackgroundColor();
  }

  public int getTextColor() {
    return themeHelper.getTextColor();
  }

  public int getSubTextColor() {
    return themeHelper.getSubTextColor();
  }

  public int getCardBackgroundColor() {
    return themeHelper.getCardBackgroundColor();
  }

  protected int getDrawerBackground() {
    return themeHelper.getDrawerBackground();
  }

  protected int getDefaultThemeToolbarColor3th() {
    return themeHelper.getDefaultThemeToolbarColor3th();
  }

  public void themeRadioButton(RadioButton radioButton) {
    themeHelper.themeRadioButton(radioButton);
  }

  public void themeCheckBox(CheckBox chk) {
    themeHelper.themeCheckBox(chk);
  }

  protected void themeButton(Button btn) {
    themeHelper.themeButton(btn);
  }

  public void setSwitchColor(int color, SwitchCompat... sw) {
    for (SwitchCompat switchCompat : sw)
      themeHelper.setSwitchCompactColor(switchCompat, color);
  }

  public void setTextViewColor(int color, TextView... textViews) {
    for (TextView txt : textViews)
      themeHelper.setTextViewColor(txt, color);
  }
}