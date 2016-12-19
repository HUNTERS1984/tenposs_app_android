package jp.tenposs.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by ambient on 12/12/16.
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

    static class FlatIcon {

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

    static class ThemifyIcon {

        public static HashMap<String, String> themifyCharacterCodes = new HashMap<String, String>();

        static {
            themifyCharacterCodes.put("ti-wand", "\ue600");
            themifyCharacterCodes.put("ti-volume", "\ue601");
            themifyCharacterCodes.put("ti-user", "\ue602");
            themifyCharacterCodes.put("ti-unlock", "\ue603");
            themifyCharacterCodes.put("ti-unlink", "\ue604");
            themifyCharacterCodes.put("ti-trash", "\ue605");
            themifyCharacterCodes.put("ti-thought", "\ue606");
            themifyCharacterCodes.put("ti-target", "\ue607");
            themifyCharacterCodes.put("ti-tag", "\ue608");
            themifyCharacterCodes.put("ti-tablet", "\ue609");
            themifyCharacterCodes.put("ti-star", "\ue60a");
            themifyCharacterCodes.put("ti-spray", "\ue60b");
            themifyCharacterCodes.put("ti-signal", "\ue60c");
            themifyCharacterCodes.put("ti-shopping-cart", "\ue60d");
            themifyCharacterCodes.put("ti-shopping-cart-full", "\ue60e");
            themifyCharacterCodes.put("ti-settings", "\ue60f");
            themifyCharacterCodes.put("ti-search", "\ue610");
            themifyCharacterCodes.put("ti-zoom-in", "\ue611");
            themifyCharacterCodes.put("ti-zoom-out", "\ue612");
            themifyCharacterCodes.put("ti-cut", "\ue613");
            themifyCharacterCodes.put("ti-ruler", "\ue614");
            themifyCharacterCodes.put("ti-ruler-pencil", "\ue615");
            themifyCharacterCodes.put("ti-ruler-alt", "\ue616");
            themifyCharacterCodes.put("ti-bookmark", "\ue617");
            themifyCharacterCodes.put("ti-bookmark-alt", "\ue618");
            themifyCharacterCodes.put("ti-reload", "\ue619");
            themifyCharacterCodes.put("ti-plus", "\ue61a");
            themifyCharacterCodes.put("ti-pin", "\ue61b");
            themifyCharacterCodes.put("ti-pencil", "\ue61c");
            themifyCharacterCodes.put("ti-pencil-alt", "\ue61d");
            themifyCharacterCodes.put("ti-paint-roller", "\ue61e");
            themifyCharacterCodes.put("ti-paint-bucket", "\ue61f");
            themifyCharacterCodes.put("ti-na", "\ue620");
            themifyCharacterCodes.put("ti-mobile", "\ue621");
            themifyCharacterCodes.put("ti-minus", "\ue622");
            themifyCharacterCodes.put("ti-medall", "\ue623");
            themifyCharacterCodes.put("ti-medall-alt", "\ue624");
            themifyCharacterCodes.put("ti-marker", "\ue625");
            themifyCharacterCodes.put("ti-marker-alt", "\ue626");
            themifyCharacterCodes.put("ti-arrow-up", "\ue627");
            themifyCharacterCodes.put("ti-arrow-right", "\ue628");
            themifyCharacterCodes.put("ti-arrow-left", "\ue629");
            themifyCharacterCodes.put("ti-arrow-down", "\ue62a");
            themifyCharacterCodes.put("ti-lock", "\ue62b");
            themifyCharacterCodes.put("ti-location-arrow", "\ue62c");
            themifyCharacterCodes.put("ti-link", "\ue62d");
            themifyCharacterCodes.put("ti-layout", "\ue62e");
            themifyCharacterCodes.put("ti-layers", "\ue62f");
            themifyCharacterCodes.put("ti-layers-alt", "\ue630");
            themifyCharacterCodes.put("ti-key", "\ue631");
            themifyCharacterCodes.put("ti-import", "\ue632");
            themifyCharacterCodes.put("ti-image", "\ue633");
            themifyCharacterCodes.put("ti-heart", "\ue634");
            themifyCharacterCodes.put("ti-heart-broken", "\ue635");
            themifyCharacterCodes.put("ti-hand-stop", "\ue636");
            themifyCharacterCodes.put("ti-hand-open", "\ue637");
            themifyCharacterCodes.put("ti-hand-drag", "\ue638");
            themifyCharacterCodes.put("ti-folder", "\ue639");
            themifyCharacterCodes.put("ti-flag", "\ue63a");
            themifyCharacterCodes.put("ti-flag-alt", "\ue63b");
            themifyCharacterCodes.put("ti-flag-alt-2", "\ue63c");
            themifyCharacterCodes.put("ti-eye", "\ue63d");
            themifyCharacterCodes.put("ti-export", "\ue63e");
            themifyCharacterCodes.put("ti-exchange-vertical", "\ue63f");
            themifyCharacterCodes.put("ti-desktop", "\ue640");
            themifyCharacterCodes.put("ti-cup", "\ue641");
            themifyCharacterCodes.put("ti-crown", "\ue642");
            themifyCharacterCodes.put("ti-comments", "\ue643");
            themifyCharacterCodes.put("ti-comment", "\ue644");
            themifyCharacterCodes.put("ti-comment-alt", "\ue645");
            themifyCharacterCodes.put("ti-close", "\ue646");
            themifyCharacterCodes.put("ti-clip", "\ue647");
            themifyCharacterCodes.put("ti-angle-up", "\ue648");
            themifyCharacterCodes.put("ti-angle-right", "\ue649");
            themifyCharacterCodes.put("ti-angle-left", "\ue64a");
            themifyCharacterCodes.put("ti-angle-down", "\ue64b");
            themifyCharacterCodes.put("ti-check", "\ue64c");
            themifyCharacterCodes.put("ti-check-box", "\ue64d");
            themifyCharacterCodes.put("ti-camera", "\ue64e");
            themifyCharacterCodes.put("ti-announcement", "\ue64f");
            themifyCharacterCodes.put("ti-brush", "\ue650");
            themifyCharacterCodes.put("ti-briefcase", "\ue651");
            themifyCharacterCodes.put("ti-bolt", "\ue652");
            themifyCharacterCodes.put("ti-bolt-alt", "\ue653");
            themifyCharacterCodes.put("ti-blackboard", "\ue654");
            themifyCharacterCodes.put("ti-bag", "\ue655");
            themifyCharacterCodes.put("ti-move", "\ue656");
            themifyCharacterCodes.put("ti-arrows-vertical", "\ue657");
            themifyCharacterCodes.put("ti-arrows-horizontal", "\ue658");
            themifyCharacterCodes.put("ti-fullscreen", "\ue659");
            themifyCharacterCodes.put("ti-arrow-top-right", "\ue65a");
            themifyCharacterCodes.put("ti-arrow-top-left", "\ue65b");
            themifyCharacterCodes.put("ti-arrow-circle-up", "\ue65c");
            themifyCharacterCodes.put("ti-arrow-circle-right", "\ue65d");
            themifyCharacterCodes.put("ti-arrow-circle-left", "\ue65e");
            themifyCharacterCodes.put("ti-arrow-circle-down", "\ue65f");
            themifyCharacterCodes.put("ti-angle-double-up", "\ue660");
            themifyCharacterCodes.put("ti-angle-double-right", "\ue661");
            themifyCharacterCodes.put("ti-angle-double-left", "\ue662");
            themifyCharacterCodes.put("ti-angle-double-down", "\ue663");
            themifyCharacterCodes.put("ti-zip", "\ue664");
            themifyCharacterCodes.put("ti-world", "\ue665");
            themifyCharacterCodes.put("ti-wheelchair", "\ue666");
            themifyCharacterCodes.put("ti-view-list", "\ue667");
            themifyCharacterCodes.put("ti-view-list-alt", "\ue668");
            themifyCharacterCodes.put("ti-view-grid", "\ue669");
            themifyCharacterCodes.put("ti-uppercase", "\ue66a");
            themifyCharacterCodes.put("ti-upload", "\ue66b");
            themifyCharacterCodes.put("ti-underline", "\ue66c");
            themifyCharacterCodes.put("ti-truck", "\ue66d");
            themifyCharacterCodes.put("ti-timer", "\ue66e");
            themifyCharacterCodes.put("ti-ticket", "\ue66f");
            themifyCharacterCodes.put("ti-thumb-up", "\ue670");
            themifyCharacterCodes.put("ti-thumb-down", "\ue671");
            themifyCharacterCodes.put("ti-text", "\ue672");
            themifyCharacterCodes.put("ti-stats-up", "\ue673");
            themifyCharacterCodes.put("ti-stats-down", "\ue674");
            themifyCharacterCodes.put("ti-split-v", "\ue675");
            themifyCharacterCodes.put("ti-split-h", "\ue676");
            themifyCharacterCodes.put("ti-smallcap", "\ue677");
            themifyCharacterCodes.put("ti-shine", "\ue678");
            themifyCharacterCodes.put("ti-shift-right", "\ue679");
            themifyCharacterCodes.put("ti-shift-left", "\ue67a");
            themifyCharacterCodes.put("ti-shield", "\ue67b");
            themifyCharacterCodes.put("ti-notepad", "\ue67c");
            themifyCharacterCodes.put("ti-server", "\ue67d");
            themifyCharacterCodes.put("ti-quote-right", "\ue67e");
            themifyCharacterCodes.put("ti-quote-left", "\ue67f");
            themifyCharacterCodes.put("ti-pulse", "\ue680");
            themifyCharacterCodes.put("ti-printer", "\ue681");
            themifyCharacterCodes.put("ti-power-off", "\ue682");
            themifyCharacterCodes.put("ti-plug", "\ue683");
            themifyCharacterCodes.put("ti-pie-chart", "\ue684");
            themifyCharacterCodes.put("ti-paragraph", "\ue685");
            themifyCharacterCodes.put("ti-panel", "\ue686");
            themifyCharacterCodes.put("ti-package", "\ue687");
            themifyCharacterCodes.put("ti-music", "\ue688");
            themifyCharacterCodes.put("ti-music-alt", "\ue689");
            themifyCharacterCodes.put("ti-mouse", "\ue68a");
            themifyCharacterCodes.put("ti-mouse-alt", "\ue68b");
            themifyCharacterCodes.put("ti-money", "\ue68c");
            themifyCharacterCodes.put("ti-microphone", "\ue68d");
            themifyCharacterCodes.put("ti-menu", "\ue68e");
            themifyCharacterCodes.put("ti-menu-alt", "\ue68f");
            themifyCharacterCodes.put("ti-map", "\ue690");
            themifyCharacterCodes.put("ti-map-alt", "\ue691");
            themifyCharacterCodes.put("ti-loop", "\ue692");
            themifyCharacterCodes.put("ti-location-pin", "\ue693");
            themifyCharacterCodes.put("ti-list", "\ue694");
            themifyCharacterCodes.put("ti-light-bulb", "\ue695");
            themifyCharacterCodes.put("ti-Italic", "\ue696");
            themifyCharacterCodes.put("ti-info", "\ue697");
            themifyCharacterCodes.put("ti-infinite", "\ue698");
            themifyCharacterCodes.put("ti-id-badge", "\ue699");
            themifyCharacterCodes.put("ti-hummer", "\ue69a");
            themifyCharacterCodes.put("ti-home", "\ue69b");
            themifyCharacterCodes.put("ti-help", "\ue69c");
            themifyCharacterCodes.put("ti-headphone", "\ue69d");
            themifyCharacterCodes.put("ti-harddrives", "\ue69e");
            themifyCharacterCodes.put("ti-harddrive", "\ue69f");
            themifyCharacterCodes.put("ti-gift", "\ue6a0");
            themifyCharacterCodes.put("ti-game", "\ue6a1");
            themifyCharacterCodes.put("ti-filter", "\ue6a2");
            themifyCharacterCodes.put("ti-files", "\ue6a3");
            themifyCharacterCodes.put("ti-file", "\ue6a4");
            themifyCharacterCodes.put("ti-eraser", "\ue6a5");
            themifyCharacterCodes.put("ti-envelope", "\ue6a6");
            themifyCharacterCodes.put("ti-download", "\ue6a7");
            themifyCharacterCodes.put("ti-direction", "\ue6a8");
            themifyCharacterCodes.put("ti-direction-alt", "\ue6a9");
            themifyCharacterCodes.put("ti-dashboard", "\ue6aa");
            themifyCharacterCodes.put("ti-control-stop", "\ue6ab");
            themifyCharacterCodes.put("ti-control-shuffle", "\ue6ac");
            themifyCharacterCodes.put("ti-control-play", "\ue6ad");
            themifyCharacterCodes.put("ti-control-pause", "\ue6ae");
            themifyCharacterCodes.put("ti-control-forward", "\ue6af");
            themifyCharacterCodes.put("ti-control-backward", "\ue6b0");
            themifyCharacterCodes.put("ti-cloud", "\ue6b1");
            themifyCharacterCodes.put("ti-cloud-up", "\ue6b2");
            themifyCharacterCodes.put("ti-cloud-down", "\ue6b3");
            themifyCharacterCodes.put("ti-clipboard", "\ue6b4");
            themifyCharacterCodes.put("ti-car", "\ue6b5");
            themifyCharacterCodes.put("ti-calendar", "\ue6b6");
            themifyCharacterCodes.put("ti-book", "\ue6b7");
            themifyCharacterCodes.put("ti-bell", "\ue6b8");
            themifyCharacterCodes.put("ti-basketball", "\ue6b9");
            themifyCharacterCodes.put("ti-bar-chart", "\ue6ba");
            themifyCharacterCodes.put("ti-bar-chart-alt", "\ue6bb");
            themifyCharacterCodes.put("ti-back-right", "\ue6bc");
            themifyCharacterCodes.put("ti-back-left", "\ue6bd");
            themifyCharacterCodes.put("ti-arrows-corner", "\ue6be");
            themifyCharacterCodes.put("ti-archive", "\ue6bf");
            themifyCharacterCodes.put("ti-anchor", "\ue6c0");
            themifyCharacterCodes.put("ti-align-right", "\ue6c1");
            themifyCharacterCodes.put("ti-align-left", "\ue6c2");
            themifyCharacterCodes.put("ti-align-justify", "\ue6c3");
            themifyCharacterCodes.put("ti-align-center", "\ue6c4");
            themifyCharacterCodes.put("ti-alert", "\ue6c5");
            themifyCharacterCodes.put("ti-alarm-clock", "\ue6c6");
            themifyCharacterCodes.put("ti-agenda", "\ue6c7");
            themifyCharacterCodes.put("ti-write", "\ue6c8");
            themifyCharacterCodes.put("ti-window", "\ue6c9");
            themifyCharacterCodes.put("ti-widgetized", "\ue6ca");
            themifyCharacterCodes.put("ti-widget", "\ue6cb");
            themifyCharacterCodes.put("ti-widget-alt", "\ue6cc");
            themifyCharacterCodes.put("ti-wallet", "\ue6cd");
            themifyCharacterCodes.put("ti-video-clapper", "\ue6ce");
            themifyCharacterCodes.put("ti-video-camera", "\ue6cf");
            themifyCharacterCodes.put("ti-vector", "\ue6d0");
            themifyCharacterCodes.put("ti-themify-logo", "\ue6d1");
            themifyCharacterCodes.put("ti-themify-favicon", "\ue6d2");
            themifyCharacterCodes.put("ti-themify-favicon-alt", "\ue6d3");
            themifyCharacterCodes.put("ti-support", "\ue6d4");
            themifyCharacterCodes.put("ti-stamp", "\ue6d5");
            themifyCharacterCodes.put("ti-split-v-alt", "\ue6d6");
            themifyCharacterCodes.put("ti-slice", "\ue6d7");
            themifyCharacterCodes.put("ti-shortcode", "\ue6d8");
            themifyCharacterCodes.put("ti-shift-right-alt", "\ue6d9");
            themifyCharacterCodes.put("ti-shift-left-alt", "\ue6da");
            themifyCharacterCodes.put("ti-ruler-alt-2", "\ue6db");
            themifyCharacterCodes.put("ti-receipt", "\ue6dc");
            themifyCharacterCodes.put("ti-pin2", "\ue6dd");
            themifyCharacterCodes.put("ti-pin-alt", "\ue6de");
            themifyCharacterCodes.put("ti-pencil-alt2", "\ue6df");
            themifyCharacterCodes.put("ti-palette", "\ue6e0");
            themifyCharacterCodes.put("ti-more", "\ue6e1");
            themifyCharacterCodes.put("ti-more-alt", "\ue6e2");
            themifyCharacterCodes.put("ti-microphone-alt", "\ue6e3");
            themifyCharacterCodes.put("ti-magnet", "\ue6e4");
            themifyCharacterCodes.put("ti-line-double", "\ue6e5");
            themifyCharacterCodes.put("ti-line-dotted", "\ue6e6");
            themifyCharacterCodes.put("ti-line-dashed", "\ue6e7");
            themifyCharacterCodes.put("ti-layout-width-full", "\ue6e8");
            themifyCharacterCodes.put("ti-layout-width-default", "\ue6e9");
            themifyCharacterCodes.put("ti-layout-width-default-alt", "\ue6ea");
            themifyCharacterCodes.put("ti-layout-tab", "\ue6eb");
            themifyCharacterCodes.put("ti-layout-tab-window", "\ue6ec");
            themifyCharacterCodes.put("ti-layout-tab-v", "\ue6ed");
            themifyCharacterCodes.put("ti-layout-tab-min", "\ue6ee");
            themifyCharacterCodes.put("ti-layout-slider", "\ue6ef");
            themifyCharacterCodes.put("ti-layout-slider-alt", "\ue6f0");
            themifyCharacterCodes.put("ti-layout-sidebar-right", "\ue6f1");
            themifyCharacterCodes.put("ti-layout-sidebar-none", "\ue6f2");
            themifyCharacterCodes.put("ti-layout-sidebar-left", "\ue6f3");
            themifyCharacterCodes.put("ti-layout-placeholder", "\ue6f4");
            themifyCharacterCodes.put("ti-layout-menu", "\ue6f5");
            themifyCharacterCodes.put("ti-layout-menu-v", "\ue6f6");
            themifyCharacterCodes.put("ti-layout-menu-separated", "\ue6f7");
            themifyCharacterCodes.put("ti-layout-menu-full", "\ue6f8");
            themifyCharacterCodes.put("ti-layout-media-right-alt", "\ue6f9");
            themifyCharacterCodes.put("ti-layout-media-right", "\ue6fa");
            themifyCharacterCodes.put("ti-layout-media-overlay", "\ue6fb");
            themifyCharacterCodes.put("ti-layout-media-overlay-alt", "\ue6fc");
            themifyCharacterCodes.put("ti-layout-media-overlay-alt-2", "\ue6fd");
            themifyCharacterCodes.put("ti-layout-media-left-alt", "\ue6fe");
            themifyCharacterCodes.put("ti-layout-media-left", "\ue6ff");
            themifyCharacterCodes.put("ti-layout-media-center-alt", "\ue700");
            themifyCharacterCodes.put("ti-layout-media-center", "\ue701");
            themifyCharacterCodes.put("ti-layout-list-thumb", "\ue702");
            themifyCharacterCodes.put("ti-layout-list-thumb-alt", "\ue703");
            themifyCharacterCodes.put("ti-layout-list-post", "\ue704");
            themifyCharacterCodes.put("ti-layout-list-large-image", "\ue705");
            themifyCharacterCodes.put("ti-layout-line-solid", "\ue706");
            themifyCharacterCodes.put("ti-layout-grid4", "\ue707");
            themifyCharacterCodes.put("ti-layout-grid3", "\ue708");
            themifyCharacterCodes.put("ti-layout-grid2", "\ue709");
            themifyCharacterCodes.put("ti-layout-grid2-thumb", "\ue70a");
            themifyCharacterCodes.put("ti-layout-cta-right", "\ue70b");
            themifyCharacterCodes.put("ti-layout-cta-left", "\ue70c");
            themifyCharacterCodes.put("ti-layout-cta-center", "\ue70d");
            themifyCharacterCodes.put("ti-layout-cta-btn-right", "\ue70e");
            themifyCharacterCodes.put("ti-layout-cta-btn-left", "\ue70f");
            themifyCharacterCodes.put("ti-layout-column4", "\ue710");
            themifyCharacterCodes.put("ti-layout-column3", "\ue711");
            themifyCharacterCodes.put("ti-layout-column2", "\ue712");
            themifyCharacterCodes.put("ti-layout-accordion-separated", "\ue713");
            themifyCharacterCodes.put("ti-layout-accordion-merged", "\ue714");
            themifyCharacterCodes.put("ti-layout-accordion-list", "\ue715");
            themifyCharacterCodes.put("ti-ink-pen", "\ue716");
            themifyCharacterCodes.put("ti-info-alt", "\ue717");
            themifyCharacterCodes.put("ti-help-alt", "\ue718");
            themifyCharacterCodes.put("ti-headphone-alt", "\ue719");
            themifyCharacterCodes.put("ti-hand-point-up", "\ue71a");
            themifyCharacterCodes.put("ti-hand-point-right", "\ue71b");
            themifyCharacterCodes.put("ti-hand-point-left", "\ue71c");
            themifyCharacterCodes.put("ti-hand-point-down", "\ue71d");
            themifyCharacterCodes.put("ti-gallery", "\ue71e");
            themifyCharacterCodes.put("ti-face-smile", "\ue71f");
            themifyCharacterCodes.put("ti-face-sad", "\ue720");
            themifyCharacterCodes.put("ti-credit-card", "\ue721");
            themifyCharacterCodes.put("ti-control-skip-forward", "\ue722");
            themifyCharacterCodes.put("ti-control-skip-backward", "\ue723");
            themifyCharacterCodes.put("ti-control-record", "\ue724");
            themifyCharacterCodes.put("ti-control-eject", "\ue725");
            themifyCharacterCodes.put("ti-comments-smiley", "\ue726");
            themifyCharacterCodes.put("ti-brush-alt", "\ue727");
            themifyCharacterCodes.put("ti-youtube", "\ue728");
            themifyCharacterCodes.put("ti-vimeo", "\ue729");
            themifyCharacterCodes.put("ti-twitter", "\ue72a");
            themifyCharacterCodes.put("ti-time", "\ue72b");
            themifyCharacterCodes.put("ti-tumblr", "\ue72c");
            themifyCharacterCodes.put("ti-skype", "\ue72d");
            themifyCharacterCodes.put("ti-share", "\ue72e");
            themifyCharacterCodes.put("ti-share-alt", "\ue72f");
            themifyCharacterCodes.put("ti-rocket", "\ue730");
            themifyCharacterCodes.put("ti-pinterest", "\ue731");
            themifyCharacterCodes.put("ti-new-window", "\ue732");
            themifyCharacterCodes.put("ti-microsoft", "\ue733");
            themifyCharacterCodes.put("ti-list-ol", "\ue734");
            themifyCharacterCodes.put("ti-linkedin", "\ue735");
            themifyCharacterCodes.put("ti-layout-sidebar-2", "\ue736");
            themifyCharacterCodes.put("ti-layout-grid4-alt", "\ue737");
            themifyCharacterCodes.put("ti-layout-grid3-alt", "\ue738");
            themifyCharacterCodes.put("ti-layout-grid2-alt", "\ue739");
            themifyCharacterCodes.put("ti-layout-column4-alt", "\ue73a");
            themifyCharacterCodes.put("ti-layout-column3-alt", "\ue73b");
            themifyCharacterCodes.put("ti-layout-column2-alt", "\ue73c");
            themifyCharacterCodes.put("ti-instagram", "\ue73d");
            themifyCharacterCodes.put("ti-google", "\ue73e");
            themifyCharacterCodes.put("ti-github", "\ue73f");
            themifyCharacterCodes.put("ti-flickr", "\ue740");
            themifyCharacterCodes.put("ti-facebook", "\ue741");
            themifyCharacterCodes.put("ti-dropbox", "\ue742");
            themifyCharacterCodes.put("ti-dribbble", "\ue743");
            themifyCharacterCodes.put("ti-apple", "\ue744");
            themifyCharacterCodes.put("ti-android", "\ue745");
            themifyCharacterCodes.put("ti-save", "\ue746");
            themifyCharacterCodes.put("ti-save-alt", "\ue747");
            themifyCharacterCodes.put("ti-yahoo", "\ue748");
            themifyCharacterCodes.put("ti-wordpress", "\ue749");
            themifyCharacterCodes.put("ti-vimeo-alt", "\ue74a");
            themifyCharacterCodes.put("ti-twitter-alt", "\ue74b");
            themifyCharacterCodes.put("ti-tumblr-alt", "\ue74c");
            themifyCharacterCodes.put("ti-trello", "\ue74d");
            themifyCharacterCodes.put("ti-stack-overflow", "\ue74e");
            themifyCharacterCodes.put("ti-soundcloud", "\ue74f");
            themifyCharacterCodes.put("ti-sharethis", "\ue750");
            themifyCharacterCodes.put("ti-sharethis-alt", "\ue751");
            themifyCharacterCodes.put("ti-reddit", "\ue752");
            themifyCharacterCodes.put("ti-pinterest-alt", "\ue753");
            themifyCharacterCodes.put("ti-microsoft-alt", "\ue754");
            themifyCharacterCodes.put("ti-linux", "\ue755");
            themifyCharacterCodes.put("ti-jsfiddle", "\ue756");
            themifyCharacterCodes.put("ti-joomla", "\ue757");
            themifyCharacterCodes.put("ti-html5", "\ue758");
            themifyCharacterCodes.put("ti-flickr-alt", "\ue759");
            themifyCharacterCodes.put("ti-email", "\ue75a");
            themifyCharacterCodes.put("ti-drupal", "\ue75b");
            themifyCharacterCodes.put("ti-dropbox-alt", "\ue75c");
            themifyCharacterCodes.put("ti-css3", "\ue75d");
            themifyCharacterCodes.put("ti-rss", "\ue75e");
            themifyCharacterCodes.put("ti-rss-alt", "\ue75f");
        }

        public static Bitmap fromThemifyIcon(AssetManager assetManager, String identifier, int textSize, int backgroundColor, int textColor) {

            String text = themifyCharacterCodes.get(identifier);
            if (text == null) {
                text = "\uE739";
            }
            Typeface font = Typeface.createFromAsset(assetManager, "fonts/themify.ttf");

            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(font);
            int textWidth = (int) paint.measureText(text);


            Bitmap bitmap = Bitmap.createBitmap(textWidth, textWidth, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
            canvas.drawText(text, 0, textWidth - (textWidth / 16), paint);
            return bitmap;
        }
    }

}