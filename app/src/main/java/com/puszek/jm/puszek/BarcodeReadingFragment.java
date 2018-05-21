package com.puszek.jm.puszek;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
import com.puszek.jm.puszek.helpers.DialogManager;
import com.puszek.jm.puszek.helpers.FieldsValidator;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.BarcodeToAdd;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.models.WasteType;
import com.puszek.jm.puszek.ui.camera.CameraSourcePreview;
import com.puszek.jm.puszek.ui.camera.GraphicOverlay;
import com.puszek.jm.puszek.utils.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeReadingFragment extends android.support.v4.app.Fragment implements BarcodeGraphicTracker.BarcodeUpdateListener {
    private static final String TAG = "Barcode-reader";


    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private Activity activity;
    private DialogManager dialogManager;
    private ArrayList<String> spinnerArray;

            View barcodeReadingFragment;
    private SharedPreferences puszekPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        barcodeReadingFragment = inflater.inflate(R.layout.fragment_barcode_reading, container, false);

        activity = getActivity();

        spinnerArray = new ArrayList<>();

        String[] spinnerItems = activity.getResources().getStringArray(R.array.waste_bar_types);
        spinnerArray.addAll(Arrays.asList(spinnerItems));

        dialogManager = new DialogManager(activity);
        mPreview = barcodeReadingFragment.findViewById(R.id.cameraPreview);
        mGraphicOverlay = barcodeReadingFragment.findViewById(R.id.graphicOverlay);

        if (PermissionManager.hasCamPermission(getActivity())) {
            createCameraSource();
        } else {
            PermissionManager.requestCamPermission(getActivity());
        }

        puszekPrefs = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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


    private void startCameraSource() throws SecurityException {
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

    String detectedBarcodeValue ="";

    @Override
    public void onBarcodeDetected(final Barcode barcode) {
        if (barcode != null) {
            final Thread newThread = new Thread(){
                @Override
                public void run() {
                    final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                    detectedBarcodeValue = barcode.displayValue;
                    String barcodeString = barcode.displayValue + "/";
                    String accessToken = "Bearer "+ puszekPrefs.getString("access_token","");


                    final Call<RequestedBarcodeData> requestBarcodeData = apiInterface.getBarcodeData(barcodeString,accessToken);

                    requestBarcodeData.enqueue(new Callback<RequestedBarcodeData>() {
                        @Override
                        public void onResponse(Call<RequestedBarcodeData> call, Response<RequestedBarcodeData> response) {

                            Log.e("BARCODE_READING","Barcode read");
                            final RequestedBarcodeData barcodeData = response.body();

                            if (barcodeData != null){
                                Log.e(TAG, "onResponse: " + response.body().getProduct().getProductName());
                                if(dialogManager.getDialog() != null) {
                                    if (!dialogManager.getDialog().isShowing())
                                        runOnUiDialog(barcodeData);
                                } else runOnUiDialog(barcodeData);
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAddBarcodeToDbDialog(detectedBarcodeValue);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<RequestedBarcodeData> call, Throwable t) {
                            runOnUiThreadToast(t.getMessage());
                        }
                    });
                }
            };

            newThread.start();

        }
    }

    private void runOnUiThreadErrorToast(final String message){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.info(activity,message).show();
            }
        });
    }

    BarcodeToAdd barcodeToAdd;
    int wasteType = 8;

    private void showAddBarcodeToDbDialog(final String detectedBarcode){
        final TextView barcodeValue;
        final EditText productName;
        final Spinner wasteTypeSpinner;
        Button saveButton, cancelButton;

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_barcode);

        barcodeValue = dialog.findViewById(R.id.bacodeValue);
        String title = getActivity().getString(R.string.value) + " " + detectedBarcode;
        barcodeValue.setText(title);

        productName = dialog.findViewById(R.id.productName);
        wasteTypeSpinner = dialog.findViewById(R.id.wasteTypeSpinner);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);
        wasteTypeSpinner.setAdapter(spinnerArrayAdapter);

        wasteTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        wasteType = 8;
                        break;
                    case 1:
                        wasteType = 7;
                        break;
                    case 2:
                        wasteType = 9;
                        break;
                    case 3:
                        wasteType = 2;
                        break;
                    case 4:
                        wasteType = 4;
                        break;
                    case 5:
                        wasteType = 5;
                        break;
                     default:
                         wasteType = 8;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancelButton = dialog.findViewById(R.id.cancelButton);
        saveButton = dialog.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldsValidator validator = new FieldsValidator(getActivity());
                if(validator.isValidField(productName)){
                  barcodeToAdd = new BarcodeToAdd();
                  barcodeToAdd.setCode(detectedBarcode);
                  barcodeToAdd.setProductName(productName.getText().toString().trim());
                  barcodeToAdd.setWasteType(wasteType);
                  addBarcodeToDb(barcodeToAdd);
                  dialog.cancel();
                } else dialog.cancel();
            }
        });
        dialog.show();
    }

    private void addBarcodeToDb(final BarcodeToAdd barcode){
        final Thread newThread = new Thread(){
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);

                final Call<RequestedBarcodeData> addBarcodeData = apiInterface.addBarcode(barcode);

                addBarcodeData.enqueue(new Callback<RequestedBarcodeData>() {
                    @Override
                    public void onResponse(Call<RequestedBarcodeData> call, Response<RequestedBarcodeData> response) {
                        runOnUiThreadToast(getActivity().getString(R.string.saved_succ));
                    }

                    @Override
                    public void onFailure(Call<RequestedBarcodeData> call, Throwable t) {
                        runOnUiThreadErrorToast(getActivity().getString(R.string.saving_error));
                    }
                });
            }
        };

        newThread.start();
    }

private void runOnUiThreadToast(final String message){
    activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toasty.info(activity,message).show();
        }
    });
}

private void runOnUiDialog(final RequestedBarcodeData barcodeData){
    activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            dialogManager.showBarcodeDetectedDialog(barcodeData);
        }
    });
}

}
