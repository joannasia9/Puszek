package com.puszek.jm.puszek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class BoxReadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View boxReadingFragment = inflater.inflate(R.layout.fragment_box_reading,container,false);

        return boxReadingFragment;
    }
}
