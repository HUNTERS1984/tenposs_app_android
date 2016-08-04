package jp.tenposs.datamodel;

import java.io.Serializable;

/**
 * Created by ambient on 7/29/16.
 */
public class HomeScreenItem extends  CommonObject implements Serializable{
    public final static long HOME_SCREEN = 0;
    public final static long MENU_SCREEN = 1;
    public final static long RESERVE_SCREEN = 2;
    public final static long NEWS_SCREEN = 3;
    public final static long PHOTO_SCREEN = 4;
    public final static long COUPON_SCREEN = 5;
    public final static long CHAT_SCREEN = 6;
    public final static long SETTING_SCREEN = 7;
    public String itemName;
    public String itemIcon;
    public long itemId;
}