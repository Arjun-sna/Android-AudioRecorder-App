package in.arjsna.audiorecorder.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.audiorecording.RecordFragment;
import in.arjsna.audiorecorder.mvpbase.BaseActivity;

public class MainActivity extends BaseActivity {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();
  private static final int PERMISSION_REQ = 222;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.main_container, RecordFragment.newInstance())
          .commit();
    }
    getPermissions();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @TargetApi(23)
  private void getPermissions() {
    if (ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
      String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.RECORD_AUDIO};
      requestPermissions(permissions, PERMISSION_REQ);
    }
  }

  @TargetApi(23)
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
        || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      showRationale(false);
    } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED
        || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
      showRationale(true);
    }
  }

  private void showRationale(boolean openSettings) {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Permissions Required")
        .setCancelable(false)
        .setMessage(
            "App requires RECORD_AUDIO permission to access mic and WRITE_EXTERNAL_STORAGE to save recorded audio")
        .setPositiveButton(R.string.dialog_action_ok, (dialog, which) -> {
          if (openSettings) {
            openSettingsPage();
          } else {
            getPermissions();
          }
          dialog.dismiss();
        })
        .setNegativeButton(R.string.dialog_action_cancel,
            (dialog, which) -> onBackPressed())
        .show();
  }

  private void openSettingsPage() {
    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, PERMISSION_REQ);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    getPermissions();
  }
}
