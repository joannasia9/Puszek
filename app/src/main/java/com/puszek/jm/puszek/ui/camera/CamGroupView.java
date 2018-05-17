package com.puszek.jm.puszek.ui.camera;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.puszek.jm.puszek.tensorflow.env.AutoFitTextureView;

import java.io.IOException;

public class CamGroupView extends ViewGroup {

    private AutoFitTextureView mTextureView;
    private GraphicOverlay mOverlay;
    private Context mContext;

    public CamGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mTextureView= new AutoFitTextureView(context);
        addView(mTextureView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(GraphicOverlay overlay) throws IOException, SecurityException {
        mOverlay = overlay;
    }
}
