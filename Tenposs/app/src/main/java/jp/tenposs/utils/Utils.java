package jp.tenposs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by ambient on 8/19/16.
 */
public class Utils {
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
        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        exitActivity();
                    }
                    break;

                    case DialogInterface.BUTTON_NEGATIVE: {
                    }
                    break;
                }
            }
        };*/

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

    public static int atoi(String input) {
        int result = 0;
        try {
            result = Integer.parseInt(input);
        } catch (Exception ignored) {

        }
        return result;
    }
}
