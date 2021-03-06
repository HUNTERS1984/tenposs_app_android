package jp.tenposs.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import jp.tenposs.staffapp.BuildConfig;
import jp.tenposs.staffapp.MainApplication;

/**
 * Created by ambient on 8/19/16.
 */
public class Utils {
    private static String Tag = "Utils";

    public static long gmtMillis() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time = cal.getTimeInMillis();
        long current = System.currentTimeMillis();

        long diff = Math.abs(current - time);

        if (diff <= 100000) {
            Assert.assertFalse(false);
        }
        return time;
    }

    static void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span, ClickableSpan clickable) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static void setTextViewHTML(TextView text, String html, ClickableSpan clickableSpan) {
        try {
            CharSequence sequence = Html.fromHtml(html);
            SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
            URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
            for (URLSpan span : urls) {
                makeLinkClickable(strBuilder, span, clickableSpan);
            }
            text.setText(strBuilder);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception ignored) {

        }
    }

    public static int getColorInt(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(resId, null);
        } else {
            return context.getResources().getColor(resId);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            try {
                if (view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception ignored) {
            }
            try {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean validateEmailAddress(String email) {
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static void showAlert(Context context,
                                 String title,
                                 String message,
                                 String positiveButton,
                                 String negativeButton,
                                 DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        if (positiveButton != null) {
            builder.setPositiveButton(positiveButton, listener);
        }
        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton, listener);
        }
        builder.show();
    }

    protected static ProgressDialog mProgressDialog;

    public static void showProgress(Context context, String message) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(20);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

//    public static void changeProgress(String message) {
//        if (mProgressDialog == null)
//            showProgress(message);
//        else
//            mProgressDialog.setMessage(message);
//    }

    public static void hideProgress() {
        try {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            mProgressDialog = null;
        } catch (Exception ignored) {
        }
    }

    public static int atoi(String input) {
        int result = 0;
        try {
            result = Integer.parseInt(input);
        } catch (Exception ignored) {

        }
        return result;
    }

    public static HashMap<String, String> fontMapping = new HashMap<String, String>();

    static {
        fontMapping.put("roboto light", "fonts/Roboto-Light.ttf");
        fontMapping.put("roboto", "fonts/Roboto-Regular.ttf");
    }

    public static Typeface getTypeFaceForFont(Context context, String fontFamily) {
        String font = fontMapping.get(fontFamily);
        if (font == null) {
            font = "fonts/Arial.ttf";
        }
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        return typeface;
    }

    public static Date dateFromString(String input) {
        Date output = null;
        try {
            SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            output = curFormatter.parse(input);
        } catch (Exception e) {
        }
        return output;
    }

    public static Date dateFromString(String input, String format) {
        Date output = null;
        try {
            SimpleDateFormat curFormatter = new SimpleDateFormat(format, Locale.US);
            output = curFormatter.parse(input);
        } catch (Exception e) {
        }
        return output;
    }

    public static String dateStringFromDate(Date date) {
        String output = "";
        try {
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            output = curFormater.format(date);
        } catch (Exception ignored) {
            SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            output = curFormater.format(date);
        }
        return output;
    }

    public static String timeStringFromDate(Date date, String outFormat) {
        String output = "";
        try {
            SimpleDateFormat curFormatter = new SimpleDateFormat(outFormat, Locale.US);
            output = curFormatter.format(date);
        } catch (Exception ignored) {
            Utils.log(ignored);
        }
        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String aToCurrency(String value) {
        int intValue = atoi(value);
        NumberFormat format = NumberFormat.getInstance();
        format.setCurrency(Currency.getInstance(Locale.JAPAN));
        return format.format(intValue);
    }

    public static String iToCurrency(int value) {
        NumberFormat format = NumberFormat.getInstance();
        format.setCurrency(Currency.getInstance(Locale.JAPAN));
        return format.format(value);
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return input;
        }
    }


    public static int colorFromHex(String hexColor, int defaultColor) {
        try {
            String color = hexColor;
            if (color.indexOf("#") != 0) {
                color = "#" + hexColor;
            }
            return Color.parseColor(color);
        } catch (Exception ex) {
            ex.printStackTrace();
            return defaultColor;
        }
    }

    public static String encodeUrlParams(String input) {
        String output = input;
        output = output.replace(" ", "%20");
        //TODO: need more special chracter
        return output;
    }

    public static String getImageUrl(String domain, String url, String defaultUrl) {
        try {
            String temp = url.toLowerCase(Locale.US);
            if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {

                return encodeUrlParams(url);
            } else {
                return domain + encodeUrlParams(url);
            }
        } catch (Exception ignored) {
            return defaultUrl;
        }
    }

    public static boolean isRealDevice(Context context) {
        boolean emu = false;

        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager.getSensorList(Sensor.TYPE_ALL).isEmpty()) {
            Log.i(Tag, "running on an emulator");
            emu = true;
        } else {
            Log.i(Tag, "running on a device");
            emu = false;
        }

        String manu = Build.MANUFACTURER;

        Log.i(Tag, "manu " + manu);

        emu |= manu.equals("unknown") || manu.equals("Genymotion");

//        return !emu;
        //TODO: debug only
        return true;
    }

    public static String getDeviceId(Context context) {
        return "";
    }


    public static String getPrefString(Context context, String key) {
        SharedPreferences preferences = SharedPreferencesHelper.getSharedPreferences(context);
        return preferences.getString(key, "");
    }

    public static boolean setPrefString(Context context, String key, String value) {
        SharedPreferences preferences = SharedPreferencesHelper.getSharedPreferences(context);
        boolean ret;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        ret = editor.commit();
        return ret;
    }
    public static int convertDpToPixel(float dp) {
        Resources resources = MainApplication.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }
    public static int getColor(Context context, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorId, null);
        } else {
            return context.getResources().getColor(colorId);
        }
    }

    public static String formatDateTime(String input, String inputFormat, String outputFormat) {
        Date date = dateFromString(input, inputFormat);
        String output;
        SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
        output = formatter.format(date);
        return output;
    }

    public static void log(Exception ignored) {
        if (BuildConfig.BUILD_TYPE.compareTo("debug") == 0) {
            Log.i("Exception", ignored.getMessage());
            ignored.printStackTrace();
        }
    }

    public static void log(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.compareTo("debug") == 0) {
            Log.i(tag, msg);
        }
    }

    public static void Assert(String msg) {
        Assert.assertFalse(msg, false);
    }
}

