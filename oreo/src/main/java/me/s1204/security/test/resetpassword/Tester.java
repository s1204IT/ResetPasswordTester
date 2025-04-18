package me.s1204.security.test.resetpassword;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Arrays;

public class Tester extends Activity implements View.OnClickListener {

    DevicePolicyManager dpm;
    ComponentName admin;

    ClipboardManager clipboard;
    ClipData clip;

    private void makeText(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private void setOnClickListener(int resId) {
        findViewById(resId).setOnClickListener(this);
    }

    private void changeLayout(int layout, int btnId) {
        setContentView(layout);
        findViewById(btnId).setOnClickListener(this);
        findViewById(R.id.backHome).setOnClickListener(this);
    }

    private String getBoxText(int resId) {
        return ((EditText) findViewById(resId)).getText().toString();
    }

    private static final int[] FUNC_LIST = {
            R.id.btn_isResetPasswordTokenActive,
            R.id.btn_setResetPasswordToken,
            R.id.btn_resetPasswordWithToken,
            R.id.btn_generateRandomPasswordToken,
            R.id.btn_isDeviceOwnerApp,
            R.id.btn_clearDeviceOwnerApp,
            R.id.btn_isProfileOwnerApp,
            R.id.btn_clearProfileOwnerApp,
            R.id.btn_isAdminActive,
            R.id.btn_removeActiveAdmin,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, DeviceAdminReceiver.class);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        for (int resId: FUNC_LIST) setOnClickListener(resId);
    }

    @Override
    public void onClick(final View v) {
        final int resId = v.getId();
        try {
            if (resId == R.id.backHome) {
                //noinspection deprecation
                onBackPressed();
            } else if (resId == R.id.btn_setResetPasswordToken) {
                changeLayout(R.layout.layout_setresetpasswordtoken, R.id.exec_setResetPasswordToken);
            } else if (resId == R.id.exec_setResetPasswordToken) {
                byte[] token = getBoxText(R.id.set_token).getBytes();
                makeText("setResetPasswordToken：" + dpm.setResetPasswordToken(admin, token));
            } else if (resId == R.id.btn_resetPasswordWithToken) {
                changeLayout(R.layout.layout_resetpasswordwithtoken, R.id.exec_resetPasswordWithToken);
            } else if (resId == R.id.exec_resetPasswordWithToken) {
                String password = getBoxText(R.id.password);
                byte[] token = getBoxText(R.id.token).getBytes();
                makeText("resetPasswordWithToken：" + dpm.resetPasswordWithToken(admin, password, token, 0));
            } else if (resId == R.id.btn_generateRandomPasswordToken) {
                byte[] randToken = SecureRandom.getInstance("SHA1PRNG").generateSeed(32);
                clip = ClipData.newPlainText("token", Arrays.toString(randToken));
                clipboard.setPrimaryClip(clip);
                makeText(Arrays.toString(randToken));
            } else if (resId == R.id.btn_isDeviceOwnerApp) {
                makeText("isDeviceOwnerApp：" + dpm.isDeviceOwnerApp(getPackageName()));
            } else if (resId == R.id.btn_clearDeviceOwnerApp) {
                //noinspection deprecation
                dpm.clearDeviceOwnerApp(getPackageName());
                makeText("clearDeviceOwnerApp executed");
            } else if (resId == R.id.btn_isProfileOwnerApp) {
                makeText("isProfileOwnerApp：" + dpm.isProfileOwnerApp(getPackageName()));
            } else if (resId == R.id.btn_clearProfileOwnerApp) {
                //noinspection deprecation
                dpm.clearProfileOwner(admin);
                makeText("clearProfileOwner executed");
            } else if (resId == R.id.btn_isAdminActive) {
                makeText("isActiveAdmin：" + dpm.isAdminActive(admin));
            } else if (resId == R.id.btn_removeActiveAdmin) {
                dpm.removeActiveAdmin(admin);
                makeText("removeActiveAdmin executed");
            } else if (resId == R.id.btn_isResetPasswordTokenActive) {
                makeText("isResetPasswordTokenActive：" + dpm.isResetPasswordTokenActive(admin));
            }
        } catch (Exception e) {
            makeText(e.getMessage());
        }
    }

    /** @noinspection DeprecatedIsStillUsed*/
    @Override
    @Deprecated
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .setPackage(getPackageName()));
    }

}
