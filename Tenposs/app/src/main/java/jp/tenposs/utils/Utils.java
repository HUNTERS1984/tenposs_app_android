package jp.tenposs.utils;

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
}
