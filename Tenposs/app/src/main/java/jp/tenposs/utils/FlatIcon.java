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
        flatIconCharacterCodes.put("flaticon-check", "\uf101");
        flatIconCharacterCodes.put("flaticon-clock", "\uf102");
        flatIconCharacterCodes.put("flaticon-close", "\uf103");
        flatIconCharacterCodes.put("flaticon-email", "\uf104");
        flatIconCharacterCodes.put("flaticon-facebook", "\uf105");
        flatIconCharacterCodes.put("flaticon-main-menu", "\uf106");
        flatIconCharacterCodes.put("flaticon-menu", "\uf107");
        flatIconCharacterCodes.put("flaticon-next", "\uf108");
        flatIconCharacterCodes.put("flaticon-phone", "\uf109");
        flatIconCharacterCodes.put("flaticon-placeholder", "\uf10a");
        flatIconCharacterCodes.put("flaticon-reload", "\uf10b");
        flatIconCharacterCodes.put("flaticon-twitter", "\uf10c");
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
