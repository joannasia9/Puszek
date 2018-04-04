package com.puszek.jm.puszek;
import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by joannamaciak on 29/03/2018.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ArchitectsDaughter.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

    }
}
