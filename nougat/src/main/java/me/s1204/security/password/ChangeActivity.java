package me.s1204.security.password;

import android.app.admin.DevicePolicyManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ChangeActivity extends Activity {

    /** @noinspection FieldCanBeLocal*/
    private final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";

    private void makeText(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Deprecated // dpm.resetPassword
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(this, DeviceAdminReceiver.class);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.change, null);
        final EditText passwordEditText = dialogView.findViewById(R.id.password);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.screenlock_password))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.change), (d, w) -> {
                    try {
                        final String passwordText = passwordEditText.getText().toString();
                        if (passwordText.isEmpty()) {
                            makeText(getString(R.string.password_empty));
                            startActivity(new Intent(ACTION_SET_NEW_PASSWORD).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else if (passwordText.length() < 4) {
                            makeText(getString(R.string.password_less_than_4));
                        } else {
                            boolean result = false;
                            result = dpm.resetPassword(passwordText, 0);
                            makeText(getString(result ? R.string.password_change_success : R.string.password_change_failed));
                        }
                    } catch (Exception e) {
                        makeText(e.getMessage());
                    } finally {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.close), (d, w) -> finish());

        if (!dpm.isAdminActive(admin)) {
            startActivity(
                new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
                    .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.admin_request_message))
            );
            finish();
        } else {
            dialog.create().show();
        }
    }

}
