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
        flatIconCharacterCodes.put("flaticon-camera", "\uf101");
        flatIconCharacterCodes.put("flaticon-chat", "\uf102");
        flatIconCharacterCodes.put("flaticon-check", "\uf103");
        flatIconCharacterCodes.put("flaticon-clock", "\uf104");
        flatIconCharacterCodes.put("flaticon-close", "\uf105");
        flatIconCharacterCodes.put("flaticon-coupon", "\uf106");
        flatIconCharacterCodes.put("flaticon-credit-card", "\uf107");
        flatIconCharacterCodes.put("flaticon-email", "\uf108");
        flatIconCharacterCodes.put("flaticon-facebook", "\uf109");
        flatIconCharacterCodes.put("flaticon-favorite", "\uf10a");
        flatIconCharacterCodes.put("flaticon-gift", "\uf10b");
        flatIconCharacterCodes.put("flaticon-home", "\uf10c");
        flatIconCharacterCodes.put("flaticon-info", "\uf10d");
        flatIconCharacterCodes.put("flaticon-like", "\uf10e");
        flatIconCharacterCodes.put("flaticon-main-menu", "\uf10f");
        flatIconCharacterCodes.put("flaticon-menu", "\uf110");
        flatIconCharacterCodes.put("flaticon-news", "\uf111");
        flatIconCharacterCodes.put("flaticon-next", "\uf112");
        flatIconCharacterCodes.put("flaticon-notify", "\uf113");
        flatIconCharacterCodes.put("flaticon-phone", "\uf114");
        flatIconCharacterCodes.put("flaticon-photo-gallery", "\uf115");
        flatIconCharacterCodes.put("flaticon-placeholder", "\uf116");
        flatIconCharacterCodes.put("flaticon-power", "\uf117");
        flatIconCharacterCodes.put("flaticon-reserve", "\uf118");
        flatIconCharacterCodes.put("flaticon-settings", "\uf119");
        flatIconCharacterCodes.put("flaticon-sign-out", "\uf11a");
        flatIconCharacterCodes.put("flaticon-staff", "\uf11b");
        flatIconCharacterCodes.put("flaticon-twitter", "\uf11c");
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
