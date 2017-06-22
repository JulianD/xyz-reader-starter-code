package com.example.xyzreader.util;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

/**
 * Created by radsen on 6/22/17.
 */

public class ColorUtilsExt {

    private static final String TAG = ColorUtilsExt.class.getSimpleName();

    private static final double MIN_CONTRAST = 2.95;

    public static int getOppositeColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);

        red = (~red) & 0xFF;
        green = (~green) & 0xFF;
        blue = (~blue) & 0xFF;

        int foreground = Color.argb(alpha, red, green, blue);

        int attempts = 0;
        double calculatedContrast = ColorUtils.calculateContrast(foreground, color);
        Log.d(TAG, String.valueOf(calculatedContrast));
        while (calculatedContrast < MIN_CONTRAST && attempts < 2){
            switch (attempts){
                case 0:
                    foreground = Color.WHITE;
                    break;
                case 1:
                    foreground = Color.BLACK;
                    break;
            }

            calculatedContrast = ColorUtils.calculateContrast(foreground, color);

            attempts++;
        }

        return foreground;
    }

}
