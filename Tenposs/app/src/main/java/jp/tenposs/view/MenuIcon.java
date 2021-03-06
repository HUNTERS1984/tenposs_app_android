package jp.tenposs.view;

/**
 * Created by ambient on 9/21/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.HashMap;

import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.MainApplication;
import jp.tenposs.tenposs.R;

public class MenuIcon {
    private static MenuIcon ourInstance = new MenuIcon();

    public static MenuIcon getInstance() {
        return ourInstance;
    }

    HashMap<String, IconRect> identifiers;

    Bitmap iconsBitmap;

    private MenuIcon() {
        iconsBitmap = BitmapFactory.decodeResource(MainApplication.getContext().getResources(), R.drawable.menu_icons);
        if (iconsBitmap == null) {
            Log.e("MOOD_ICON", "Error decode bitmap from drawable");
        }
        if (identifiers == null) {
            identifiers = new HashMap<String, IconRect>() {{
                int y = 31;
                int h = 38;
                int w = 38;
                int x = 0;

                put("icon-home", new IconRect(x, y, w, h));
                y += 100;

                put("icon-menu", new IconRect(x, y, w, h));
                y += 100;
                put("icon-reserve", new IconRect(x, y, w, h));
                y += 100;
                put("icon-news", new IconRect(x, y, w, h));
                y += 100;
                put("icon-photo", new IconRect(x, y, w, h));
                y += 100;
                put("icon-staff", new IconRect(x, y, w, h));
                y += 100;
                put("icon-coupon", new IconRect(x, y, w, h));
                y += 100;
                put("icon-chat", new IconRect(x, y, w, h));
                y += 100;
                put("icon-setting", new IconRect(x, y, w, h));
                y += 100;
            }};
        }
    }

    public Bitmap getIconBitmapWithIdentifier(String id) {
        IconRect iconRect = identifiers.get(id);
        Bitmap icon = null;
        if (iconRect != null) {
            icon = Bitmap.createBitmap(iconsBitmap, iconRect.x, iconRect.y, iconRect.width, iconRect.height);
        }
        return icon;
    }

    public Bitmap getIconBitmapWithId(int menuId) {
        String menuName = "";
        switch (menuId) {
            case AbstractFragment.TOP_SCREEN: {
                menuName = "icon-home";
            }
            break;
            case AbstractFragment.MENU_SCREEN: {
                menuName = "icon-menu";
            }
            break;
            case AbstractFragment.NEWS_SCREEN: {
                menuName = "icon-news";
            }
            break;
            case AbstractFragment.RESERVE_SCREEN: {
                menuName = "icon-reserve";
            }
            break;
            case AbstractFragment.PHOTO_SCREEN: {
                menuName = "icon-photo";
            }
            break;
            case AbstractFragment.CHAT_SCREEN: {
                menuName = "icon-chat";
            }
            break;
            case AbstractFragment.STAFF_SCREEN: {
                menuName = "icon-staff";
            }
            break;
            case AbstractFragment.COUPON_SCREEN: {
                menuName = "icon-coupon";
            }
            break;
            case AbstractFragment.SETTING_SCREEN: {
                menuName = "icon-setting";
            }
            break;
            //case AbstractFragment.SIGN_OUT_SCREEN: {
            default:
//                menuName = "icon-sign-out";
                return null;

        }

        return getIconBitmapWithIdentifier(menuName);
    }

    public Drawable getIconDrawableWithId(Context context, int menuId) {
        int iconId;
        switch (menuId) {
            case AbstractFragment.TOP_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_home;
            }
            break;
            case AbstractFragment.MENU_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_menu;
            }
            break;
            case AbstractFragment.NEWS_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_news;
            }
            break;
            case AbstractFragment.RESERVE_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_reserve;
            }
            break;
            case AbstractFragment.PHOTO_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_photos;
            }
            break;
            case AbstractFragment.CHAT_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_chat;
            }
            break;
            case AbstractFragment.STAFF_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_staff;
            }
            break;
            case AbstractFragment.COUPON_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_coupon;
            }
            break;
            case AbstractFragment.SETTING_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_setting;
            }
            break;
            //case AbstractFragment.SIGN_OUT_SCREEN: {
            default:
//                menuName = "icon-sign-out";
                return null;
        }

        Drawable icon;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon = context.getResources().getDrawable(iconId, null);
        } else {
            icon = context.getResources().getDrawable(iconId);
        }
        return icon;
    }

    public Drawable getHomeIconDrawableWithId(Context context, int menuId) {
        int iconId;
        switch (menuId) {
            case AbstractFragment.TOP_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_home;
            }
            break;
            case AbstractFragment.MENU_SCREEN: {
                iconId = R.drawable.restaurant_ic_menu;
            }
            break;
            case AbstractFragment.NEWS_SCREEN: {
                iconId = R.drawable.restaurant_ic_news;
            }
            break;
            case AbstractFragment.RESERVE_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_reserve;
            }
            break;
            case AbstractFragment.PHOTO_SCREEN: {
                iconId = R.drawable.restaurant_ic_photos;
            }
            break;
            case AbstractFragment.CHAT_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_chat;
            }
            break;
            case AbstractFragment.STAFF_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_staff;
            }
            break;
            case AbstractFragment.COUPON_SCREEN: {
                iconId = R.drawable.restaurant_ic_coupon;
            }
            break;
            case AbstractFragment.SETTING_SCREEN: {
                iconId = R.drawable.restaurant_ic_sm_setting;
            }
            break;
            default:
                return null;
        }

        Drawable icon;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon = context.getResources().getDrawable(iconId, null);
        } else {
            icon = context.getResources().getDrawable(iconId);
        }
        return icon;
    }

    class IconRect {
        public int x, y, width, height;

        IconRect(int x, int y, int width, int height) {
            this.x = (int) convertDpToPixel(x);
            this.y = (int) convertDpToPixel(y);
            this.width = (int) convertDpToPixel(width);
            this.height = (int) convertDpToPixel(height);
        }

        float convertDpToPixel(float dp) {
            Resources resources = MainApplication.getContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return px;
        }
    }
}