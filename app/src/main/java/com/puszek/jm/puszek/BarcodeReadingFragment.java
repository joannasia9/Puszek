package com.puszek.jm.puszek;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.puszek.jm.puszek.helpers.BarcodeGraphic;
import com.puszek.jm.puszek.helpers.BarcodeGraphicTracker;
import com.puszek.jm.puszek.helpers.BarcodeTrackerFactory;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.ui.camera.CameraSourcePreview;
import com.puszek.jm.puszek.ui.camera.GraphicOverlay;
import com.puszek.jm.puszek.utils.PermissionManager;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeReadingFragment extends android.support.v4.app.Fragment implements BarcodeGraphicTracker.BarcodeUpdateListener {
    private static final String TAG = "Barcode-reader";


    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    View barcodeReadingFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        barcodeReadingFragment = inflater.inflate(R.layout.fragment_barcode_reading, container, false);

        mPreview = barcodeReadingFragment.findViewById(R.id.cameraPreview);
        mGraphicOverlay = barcodeReadingFragment.findViewById(R.id.graphicOverlay);

        if (PermissionManager.hasCamPermission(getActivity())) {
            createCameraSource();
        } else {
            PermissionManager.requestCamPermission(getActivity());
        }

        return barcodeReadingFragment;
    }


    private void createCameraSource() {
        Context context = getContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.EAN_8 | Barcode.EAN_13 | Barcode.CODE_128)
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);

        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getContext(), R.string.error_low_storage, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "ERROR low storage");
            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(2064, 1548)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(20.0f);


        mCameraSource = builder.build();

    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
            if (PermissionManager.managedCamPermissions(requestCode,grantResults)) startCameraSource();
    }


    private  void startCameraSource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, PermissionManager.RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onBarcodeDetected(final Barcode barcode) {
        if (barcode!=null) {

            final Thread newThread = new Thread(){
                @Override
                public void run() {
                    final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                    String barcodeString = barcode.displayValue + "/";
                    final Call<RequestedBarcodeData> requestBarcodeData = apiInterface.getBarcodeData(barcodeString);

                    requestBarcodeData.enqueue(new Callback<RequestedBarcodeData>() {
                        @Override
                        public void onResponse(Call<RequestedBarcodeData> call, Response<RequestedBarcodeData> response) {

                            Log.e("BARCODE_READING","Barcode read");
                            final RequestedBarcodeData barcodeData = response.body();

                            if (barcodeData != null){
                                Log.e(TAG, "onResponse: " + response.body().getProduct().getProductName());
                            runOnUiThreadToast(getActivity(), barcodeData.getProduct().getProductName());
                            } else runOnUiThreadToast(getActivity(),getActivity().getString(R.string.no_result));
                        }

                        @Override
                        public void onFailure(Call<RequestedBarcodeData> call, Throwable t) {
                            runOnUiThreadToast(getActivity(), t.getMessage());
                        }
                    });
                }
            };

            newThread.start();

        }
    }

private void runOnUiThreadToast(Activity activity,final String message){
    activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toasty.info(getActivity(),message).show();
        }
    });
}
}
