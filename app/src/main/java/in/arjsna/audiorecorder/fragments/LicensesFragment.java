package in.arjsna.audiorecorder.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import in.arjsna.audiorecorder.R;

public class LicensesFragment extends DialogFragment {
  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater dialogInflater = getActivity().getLayoutInflater();
    View openSourceLicensesView = dialogInflater.inflate(R.layout.fragment_licenses, null);

    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
    dialogBuilder.setView(openSourceLicensesView)
        .setTitle((getString(R.string.dialog_title_licenses)))
        .setNeutralButton(android.R.string.ok, null);

    return dialogBuilder.create();
  }
}
