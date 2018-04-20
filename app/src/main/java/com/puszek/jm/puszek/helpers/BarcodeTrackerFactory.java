package com.puszek.jm.puszek.helpers;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.puszek.jm.puszek.BarcodeReadingFragment;
import com.puszek.jm.puszek.ui.camera.GraphicOverlay;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private BarcodeReadingFragment mFragment;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> mGraphicOverlay,
                                 BarcodeReadingFragment mFragment) {
        this.mGraphicOverlay = mGraphicOverlay;
        this.mFragment = mFragment;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic, mFragment);
    }

}