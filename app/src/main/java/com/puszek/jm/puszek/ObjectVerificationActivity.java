package com.puszek.jm.puszek;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;

import com.puszek.jm.puszek.helpers.DialogManager;
import com.puszek.jm.puszek.helpers.OnActivityStatusChangedListener;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.models.WasteType;
import com.puszek.jm.puszek.ui.camera.CameraActivity;
import com.puszek.jm.puszek.tf.Classifier;
import com.puszek.jm.puszek.tf.OverlayView;
import com.puszek.jm.puszek.tf.TensorflowImageClassifier;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectVerificationActivity extends CameraActivity implements ImageReader.OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    protected static final boolean SAVE_PREVIEW_BITMAP = false;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private long lastProcessingTimeMs;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Placeholder";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/output_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/output_labels.txt";

    private static final boolean MAINTAIN_ASPECT = true;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private Integer sensorOrientation;
    private Classifier classifier;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private BorderedText borderedText;
    private DialogManager dialogManager;
    private OnActivityStatusChangedListener onActivityStatusChangedListener;

    public OnActivityStatusChangedListener getOnActivityStatusChangedListener() {
        return onActivityStatusChangedListener;
    }

    public void setOnActivityStatusChangedListener(OnActivityStatusChangedListener onActivityStatusChangedListener) {
        this.onActivityStatusChangedListener = onActivityStatusChangedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentDate();
        dialogManager = new DialogManager(this);
        onActivityStatusChangedListener.OnActivityStatusChanged(true);
        requestWasteTypes();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reading;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    private static final float TEXT_SIZE_DIP = 10;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        classifier =
                TensorflowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                INPUT_SIZE, INPUT_SIZE,
                sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        renderDebug(canvas);
                    }
                });
    }

    @Override
    protected void processImage() {
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        LOGGER.i("Detect: %s", results);

                        if (results.size() != 0) {
                            Log.i("OBJECT DETECTED", results.get(0).getTitle());

                            if (results.get(0).getConfidence()>0.5){
                                lastResult = results.get(0).getTitle();
                                if (dialogManager.getDialog() != null ) {
                                    if (!dialogManager.getDialog().isShowing()) {
                                        runOnUiDialog(results.get(0).getTitle());
                                    }
                                } else {
                                    runOnUiDialog(results.get(0).getTitle());
                                }
                            }

                        }
                            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                            requestRender();
                            readyForNextImage();
                    }
                });
    }
String lastResult = "";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(dialogManager.getDialog()!=null){
            if (dialogManager.getDialog().isShowing()) dialogManager.getDialog().dismiss();
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        onActivityStatusChangedListener.OnActivityStatusChanged(false);
        if(dialogManager.getDialog() != null) dialogManager.getDialog().dismiss();
    }

    @Override
    public synchronized void onStop() {
        super.onStop();
        onActivityStatusChangedListener.OnActivityStatusChanged(false);
        if(dialogManager.getDialog() != null) dialogManager.getDialog().dismiss();
    }


    Date currentDate;
    private void setCurrentDate(){
        Calendar c = Calendar.getInstance();
        currentDate = c.getTime();
    }

    private void runOnUiDialog(final String results){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogManager.showBoxDetectedDialog(results,wasteTypes, currentDate);
            }
        });
    }

    @Override
    public void onSetDebug(boolean debug) {
        classifier.enableStatLogging(debug);
    }

    private void renderDebug(final Canvas canvas) {
        if (!isDebug()) {
            return;
        }
        final Bitmap copy = cropCopyBitmap;
        if (copy != null) {
            final Matrix matrix = new Matrix();
            final float scaleFactor = 2;
            matrix.postScale(scaleFactor, scaleFactor);
            matrix.postTranslate(
                    canvas.getWidth() - copy.getWidth() * scaleFactor,
                    canvas.getHeight() - copy.getHeight() * scaleFactor);
            canvas.drawBitmap(copy, matrix, new Paint());

            final Vector<String> lines = new Vector<String>();
            if (classifier != null) {
                String statString = classifier.getStatString();
                String[] statLines = statString.split("\n");
                for (String line : statLines) {
                    lines.add(line);
                }
            }

            lines.add("Frame: " + previewWidth + "x" + previewHeight);
            lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
            lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
            lines.add("Rotation: " + sensorOrientation);
            lines.add("Inference time: " + lastProcessingTimeMs + "ms");

            borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
        }
    }

    WasteType[] wasteTypes = null;

    private void requestWasteTypes(){
        final Thread newThread = new Thread(){
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                SharedPreferences puszekPrefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String accessToken = "Bearer "+ puszekPrefs.getString("access_token","");

                final Call<WasteType[]> requestWasteType = apiInterface.getWasteTypes(accessToken);

                requestWasteType.enqueue(new Callback<WasteType[]>() {
                    @Override
                    public void onResponse(Call<WasteType[]> call, Response<WasteType[]> response) {
                        wasteTypes = response.body();

                    }

                    @Override
                    public void onFailure(Call<WasteType[]> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        };

        newThread.start();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        onActivityStatusChangedListener.OnActivityStatusChanged(true);
    }
}
