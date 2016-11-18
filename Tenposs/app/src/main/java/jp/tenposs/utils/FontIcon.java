package jp.tenposs.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

/**
 * Created by ambient on 10/24/16.
 */

public class FontIcon {

    public static int FLATICON = 1;
    public static int THEMIFY = 2;

    public static Bitmap imageForFontIdentifier(AssetManager assetManager, String identifier, int textSize, int backgroundColor, int textColor, int fontType) {
        if (fontType == FLATICON) {
            return FlatIcon.fromFlatIcon(assetManager, identifier, Utils.convertDpToPixel(textSize), backgroundColor, textColor);
        } else if (fontType == THEMIFY) {
            return ThemifyIcon.fromThemifyIcon(assetManager, identifier, Utils.convertDpToPixel(textSize), backgroundColor, textColor);
        } else return null;
    }
}
