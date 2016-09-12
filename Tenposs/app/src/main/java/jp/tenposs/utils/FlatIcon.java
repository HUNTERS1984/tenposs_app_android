package jp.tenposs.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by ambient on 7/28/16.
 */
public class FlatIcon {

    public static HashMap<String, String> flatIconCharacterCodes = new HashMap<String, String>();

    static {
        flatIconCharacterCodes.put("flaticon-back", "\uf100");
        flatIconCharacterCodes.put("flaticon-chat", "\uf101");
        flatIconCharacterCodes.put("flaticon-clock", "\uf102");
        flatIconCharacterCodes.put("flaticon-close", "\uf103");
        flatIconCharacterCodes.put("flaticon-coupon", "\uf104");
        flatIconCharacterCodes.put("flaticon-email", "\uf105");
        flatIconCharacterCodes.put("flaticon-facebook", "\uf106");
        flatIconCharacterCodes.put("flaticon-home", "\uf107");
        flatIconCharacterCodes.put("flaticon-main-menu", "\uf108");
        flatIconCharacterCodes.put("flaticon-menu", "\uf109");
        flatIconCharacterCodes.put("flaticon-news", "\uf10a");
        flatIconCharacterCodes.put("flaticon-next", "\uf10b");
        flatIconCharacterCodes.put("flaticon-phone", "\uf10c");
        flatIconCharacterCodes.put("flaticon-photo-gallery", "\uf10d");
        flatIconCharacterCodes.put("flaticon-placeholder", "\uf10e");
        flatIconCharacterCodes.put("flaticon-reserve", "\uf10f");
        flatIconCharacterCodes.put("flaticon-settings", "\uf110");
        flatIconCharacterCodes.put("flaticon-sign-out", "\uf111");
        flatIconCharacterCodes.put("flaticon-staff", "\uf112");
        flatIconCharacterCodes.put("flaticon-twitter", "\uf113");
    }

    public static Bitmap fromFlatIcon(AssetManager assetManager, String identifier, int textSize, int backgroundColor, int textColor) {

        String text = flatIconCharacterCodes.get(identifier);
        if (text == null) {
            text = "\uF103";
        }
        Typeface font = Typeface.createFromAsset(assetManager, "fonts/Flaticon.ttf");

        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(font);
        int textWidth = (int) textPaint.measureText(text);


        Bitmap bitmap = Bitmap.createBitmap(textWidth, textWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //int xPos = (canvas.getWidth() / 2);
        int xPos = 0;
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        canvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
        //canvas.drawText(text, 0, textWidth - (textWidth / 16), textPaint);
        canvas.drawText(text, xPos, yPos, textPaint);
//        canvas.drawText(text, 0, 0, paint);
        return bitmap;
    }
}
