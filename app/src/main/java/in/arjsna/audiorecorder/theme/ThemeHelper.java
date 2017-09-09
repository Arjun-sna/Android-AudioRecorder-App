package in.arjsna.audiorecorder.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.orhanobut.hawk.Hawk;
import in.arjsna.audiorecorder.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ThemeHelper {

  private final Context context;

  Theme baseTheme;
  private int primaryColor;
  private int accentColor;
  private int[] layerColor;

  private ThemeHelper(Context context) {
    this.context = context;
  }

  public static ThemeHelper getInstance(Context context) {
    return new ThemeHelper(context);
  }

  public static ThemeHelper getInstanceLoaded(Context context) {
    ThemeHelper instance = getInstance(context);
    instance.updateTheme();
    return instance;
  }

  public void updateTheme() {
    this.primaryColor = Hawk.get(context.getString(R.string.preference_primary_color),
        getColor(R.color.av_color5));
    this.accentColor = Hawk.get(context.getString(R.string.preference_accent_color),
        getColor(R.color.av_color5));
    this.layerColor = Hawk.get(context.getString(R.string.preference_layer_colors),
        ColorPalette.getColors(context, ContextCompat.getColor(context, R.color.av_color5)));
    baseTheme = Theme.fromValue(Hawk.get(context.getString(R.string.preference_base_theme), 1));
  }

  public int[] getLayerColor() {
    return layerColor;
  }

  public int getPrimaryColor() {
    return primaryColor;
  }

  public int getAccentColor() {
    return accentColor;
  }

  public Theme getBaseTheme() {
    return baseTheme;
  }

  public void setBaseTheme(Theme baseTheme) {
    this.baseTheme = baseTheme;
    Hawk.put(context.getString(R.string.preference_base_theme), getBaseTheme().getValue());
  }

  public static int getPrimaryColor(Context context) {
    return Hawk.get(context.getString(R.string.preference_primary_color),
        ContextCompat.getColor(context, R.color.md_indigo_500));
  }

  public boolean isPrimaryEqualAccent() {
    return (this.primaryColor == this.accentColor);
  }

  public static int getAccentColor(Context context) {
    return Hawk.get(context.getString(R.string.preference_accent_color),
        ContextCompat.getColor(context, R.color.md_light_blue_500));
  }

  public static Theme getBaseTheme(Context context) {
    return Theme.fromValue(
        Hawk.get(context.getString(R.string.preference_base_theme), Theme.LIGHT.value));
  }

  public int getColor(@ColorRes int color) {
    return ContextCompat.getColor(context, color);
  }

  private static int getColor(Context context, @ColorRes int color) {
    return ContextCompat.getColor(context, color);
  }

  public void themeSeekBar(SeekBar bar) {
    bar.getProgressDrawable()
        .setColorFilter(new PorterDuffColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN));
    bar.getThumb()
        .setColorFilter(new PorterDuffColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN));
  }

  public int getBackgroundColor() {
    switch (baseTheme) {
      case DARK:
        return getColor(R.color.md_dark_background);
      case AMOLED:
        return getColor(R.color.md_black_1000);
      case LIGHT:
      default:
        return getColor(R.color.md_light_background);
    }
  }

  public int getInvertedBackgroundColor() {
    switch (baseTheme) {
      case DARK:
      case AMOLED:
        return getColor(R.color.md_light_background);
      case LIGHT:
      default:
        return getColor(R.color.md_dark_background);
    }
  }

  public int getTextColor() {
    switch (baseTheme) {
      case DARK:
      case AMOLED:
        return getColor(R.color.md_grey_200);
      case LIGHT:
      default:
        return getColor(R.color.md_grey_800);
    }
  }

  public int getSubTextColor() {
    switch (baseTheme) {
      case DARK:
      case AMOLED:
        return getColor(R.color.md_grey_400);
      case LIGHT:
      default:
        return getColor(R.color.md_grey_600);
    }
  }

  public int getCardBackgroundColor() {
    switch (baseTheme) {
      case DARK:
        return getColor(R.color.md_dark_cards);
      case AMOLED:
        return getColor(R.color.md_black_1000);
      case LIGHT:
      default:
        return getColor(R.color.md_light_cards);
    }
  }

  public int getButtonBackgroundColor() {
    switch (baseTheme) {
      case DARK:
        return getColor(R.color.md_grey_700);
      case AMOLED:
        return getColor(R.color.md_grey_900);
      case LIGHT:
      default:
        return getColor(R.color.md_grey_200);
    }
  }

  public int getDrawerBackground() {
    switch (baseTheme) {
      case DARK:
        return getColor(R.color.md_dark_cards);
      case AMOLED:
        return getColor(R.color.md_black_1000);
      case LIGHT:
      default:
        return getColor(R.color.md_light_cards);
    }
  }

  public int getDefaultThemeToolbarColor3th() {
    switch (baseTheme) {
      case DARK:
        return getColor(R.color.md_black_1000);
      case LIGHT:
      default:
      case AMOLED:
        return getColor(R.color.md_blue_grey_800);
    }
  }

  private ColorStateList getTintList() {
    return new ColorStateList(
        new int[][] {
            new int[] {-android.R.attr.state_enabled}, //disabled
            new int[] {android.R.attr.state_enabled} //enabled
        }, new int[] {getTextColor(), getAccentColor()});
  }

  public void themeRadioButton(RadioButton radioButton) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      radioButton.setButtonTintList(getTintList());
      radioButton.setTextColor(getTextColor());
    }
  }

  public void themeCheckBox(CheckBox chk) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      chk.setButtonTintList(getTintList());
      chk.setTextColor(getTextColor());
    }
  }

  public void themeButton(Button btn) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      btn.setTextColor(getTextColor());
      btn.setBackgroundColor(getButtonBackgroundColor());
    }
  }

  public void setSwitchCompactColor(SwitchCompat sw, int color) {
    /** SWITCH HEAD **/
    sw.getThumbDrawable().setColorFilter(
        sw.isChecked() ? color :
            (baseTheme.equals(Theme.LIGHT) ? getColor(R.color.md_grey_200)
                : getColor(R.color.md_grey_600)),
        PorterDuff.Mode.MULTIPLY);
    /** SWITCH BODY **/
    sw.getTrackDrawable().setColorFilter(
        sw.isChecked() ? ColorPalette.getTransparentColor(color, 100) :
            (baseTheme.equals(Theme.LIGHT) ? getColor(R.color.md_grey_400)
                : getColor(R.color.md_grey_900)),
        PorterDuff.Mode.MULTIPLY);
  }

  public void setTextViewColor(TextView txt, int color) {
    txt.setTextColor(color);
  }

  public void setScrollViewColor(ScrollView scr) {
    try {
      Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
      mScrollCacheField.setAccessible(true);
      Object mScrollCache = mScrollCacheField.get(scr); // scr is your Scroll View

      Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
      scrollBarField.setAccessible(true);
      Object scrollBar = scrollBarField.get(mScrollCache);

      Method method =
          scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
      method.setAccessible(true);

      ColorDrawable ColorDraw = new ColorDrawable(getPrimaryColor());
      method.invoke(scrollBar, ColorDraw);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setColorScrollBarDrawable(Drawable drawable) {
    drawable.setColorFilter(new PorterDuffColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP));
  }

  public static void setCursorColor(EditText editText, int color) {
    try {
      Field fCursorDrawableRes =
          TextView.class.getDeclaredField("mCursorDrawableRes");
      fCursorDrawableRes.setAccessible(true);
      int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
      Field fEditor = TextView.class.getDeclaredField("mEditor");
      fEditor.setAccessible(true);
      Object editor = fEditor.get(editText);
      Class<?> clazz = editor.getClass();
      Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
      fCursorDrawable.setAccessible(true);

      Drawable[] drawables = new Drawable[2];
      drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
      drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
      drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
      drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
      fCursorDrawable.set(editor, drawables);
    } catch (final Throwable ignored) {
    }
  }
}
