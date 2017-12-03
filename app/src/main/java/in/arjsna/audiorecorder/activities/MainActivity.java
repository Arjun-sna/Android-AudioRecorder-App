package in.arjsna.audiorecorder.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import com.squareup.haha.perflib.Main;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.audiorecording.RecordFragment;
import in.arjsna.audiorecorder.mvpbase.BaseActivity;
import java.util.List;
import javax.inject.Inject;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity
    implements HasSupportFragmentInjector, EasyPermissions.PermissionCallbacks {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();
  private static final int PERMISSION_REQ = 222;

  @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

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
    String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO};
    if (!EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
      EasyPermissions.requestPermissions(this, getString(R.string.permissions_required),
          PERMISSION_REQ, permissions);
    }
  }

  @TargetApi(23)
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  private void showRationale() {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Permissions Required")
        .setCancelable(false)
        .setMessage(
            getString(R.string.permissions_required))
        .setPositiveButton(R.string.dialog_action_ok, (dialog, which) -> {
          openSettingsPage();
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

  @Override public AndroidInjector<Fragment> supportFragmentInjector() {
    return dispatchingAndroidInjector;
  }

  @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
    //NO-OP
  }

  @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      showRationale();
      return;
    }
    finish();
  }
}
