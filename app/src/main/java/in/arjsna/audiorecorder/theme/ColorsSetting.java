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
        dialogLayout.findViewById(R.id.color_picker_primary);
    final LineColorPicker colorPicker2 =
        dialogLayout.findViewById(R.id.color_picker_primary_2);
    final TextView dialogTitle = dialogLayout.findViewById(R.id.dialog_title);
    dialogTitle.setText(title);
    dialogTitle.setBackgroundColor(getActivity().getPrimaryColor());
    colorPicker.setSelected(true);
    colorPicker.setOnColorChangedListener(c -> {
      dialogTitle.setBackgroundColor(c);
      colorPicker2.setColors(ColorPalette.getColors(getActivity(), colorPicker.getColor()));
      colorPicker2.setSelectedColor(colorPicker.getColor());
      dialogTitle.setBackgroundColor(colorPicker2.getColors()[0]);
      chooser.onColorChanged(colorPicker2.getColors()[0]);
    });

    int[] baseColors = ColorPalette.getBaseColors(getActivity());
    colorPicker.setColors(baseColors);
    colorPicker.setSelectedColor(defaultColor);

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
          selectedColor.colorPrimary = colorPicker.getColor();
          selectedColor.shades = colorPicker2.getColors();
          chooser.onColorSelected(selectedColor);
        });

    dialogBuilder.setOnDismissListener(dialog -> chooser.onDialogDismiss());
    dialogBuilder.show();
  }
}
