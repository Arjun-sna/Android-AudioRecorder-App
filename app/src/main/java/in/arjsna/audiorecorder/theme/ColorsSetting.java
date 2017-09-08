package in.arjsna.audiorecorder.theme;

import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import in.arjsna.audiorecorder.R;
import uz.shift.colorpicker.LineColorPicker;

public class ColorsSetting extends ThemedSetting {

  public ColorsSetting(ThemedActivity activity) {
    super(activity);
  }

  public static class SelectedColor {
    public int colorPrimary;
    public int[] shades;
  }

  public interface ColorChooser {
    void onColorSelected(SelectedColor color);

    void onDialogDismiss();

    void onColorChanged(int color);
  }

  public void chooseColor(@StringRes int title, final ColorChooser chooser, int defaultColor) {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

    View dialogLayout =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_color_picker, null);
    final LineColorPicker colorPicker =
        (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_primary);
    final LineColorPicker colorPicker2 =
        (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_primary_2);
    final TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.dialog_title);
    dialogTitle.setText(title);
    //colorPicker2.setOnColorChangedListener(c -> {
    //  dialogTitle.setBackgroundColor(c);
    //  chooser.onColorChanged(c);
    //});

    colorPicker.setOnColorChangedListener(c -> {
      colorPicker2.setColors(ColorPalette.getColors(getActivity(), colorPicker.getColor()));
      colorPicker2.setSelectedColor(colorPicker.getColor());
      dialogTitle.setBackgroundColor(colorPicker2.getColors()[0]);
      chooser.onColorChanged(colorPicker2.getColors()[0]);
    });

    int[] baseColors = ColorPalette.getBaseColors(getActivity());
    colorPicker.setColors(baseColors);

    for (int i : baseColors) {
      for (int i2 : ColorPalette.getColors(getActivity(), i))
        if (i2 == defaultColor) {
          colorPicker.setSelectedColor(i);
          colorPicker2.setColors(ColorPalette.getColors(getActivity(), i));
          colorPicker2.setSelectedColor(i2);
          break;
        }
    }

    dialogBuilder.setView(dialogLayout);

    dialogBuilder.setNegativeButton(
        getActivity().getString(R.string.dialog_action_cancel).toUpperCase(),
        (dialog, which) -> {
          dialog.cancel();
          chooser.onDialogDismiss();
        });

    dialogBuilder.setPositiveButton(
        getActivity().getString(R.string.dialog_action_ok).toUpperCase(),
        (dialog, which) -> {
          AlertDialog alertDialog = (AlertDialog) dialog;
          alertDialog.setOnDismissListener(null);
          SelectedColor selectedColor = new SelectedColor();
          selectedColor.colorPrimary = colorPicker2.getColors()[0];
          selectedColor.shades = colorPicker2.getColors();
          chooser.onColorSelected(selectedColor);
        });

    dialogBuilder.setOnDismissListener(dialog -> chooser.onDialogDismiss());
    dialogBuilder.show();
  }
}
