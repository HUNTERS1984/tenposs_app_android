package jp.tenposs.datamodel;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 12/13/16.
 */

public class NotificationInfo {
    public static final String COUPON = "coupon";
    public static final String NEWS = "news";
    public static final String RANKING = "ranking";
    public String subtitle;
    public String msgcnt;
    public String image_url;
    public String id;
    public String type;
    public String title;
    public String vibrate;
    public String message;
    public String tickerText;

    public boolean isNewsNotification() {
        boolean ret = false;
        try {
            ret = this.type.compareTo(NEWS) == 0;
        } catch (Exception ignored) {
            Utils.log(ignored);
        }
        return ret;
    }

    public boolean isCouponNotification() {
        boolean ret = false;
        try {
            ret = this.type.compareTo(COUPON) == 0;
        } catch (Exception ignored) {
            Utils.log(ignored);
        }
        return ret;
    }
}
