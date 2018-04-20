package com.puszek.jm.puszek.utils;

import java.util.ArrayList;

public interface PermissionResultCallback {
    void PermissionGranted(int requestCode);

    void PartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions);

    void PermissionDenied(int requestCode);

    void NeverAskAgain(int requestCode);

}
