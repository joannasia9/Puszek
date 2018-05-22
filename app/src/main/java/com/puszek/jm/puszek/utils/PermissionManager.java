package com.puszek.jm.puszek.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import com.puszek.jm.puszek.R;
import es.dmoral.toasty.Toasty;
public class PermissionManager {

    private static final String TAG = "PermissionsManager";
    public static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final int RC_HANDLE_GMS = 9001;

    public static boolean hasCamPermission(Activity activity) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestCamPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                    activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toasty.info(activity,activity.getString(R.string.req_access)).show();
            }
            activity.requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_HANDLE_CAMERA_PERM);
        }
    }

    public static boolean managedCamPermissions(int requestCode,
                                         int[] grantResults){
        return (requestCode == RC_HANDLE_CAMERA_PERM && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED);
    }
}
