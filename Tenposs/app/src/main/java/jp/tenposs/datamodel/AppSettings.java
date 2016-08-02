package jp.tenposs.datamodel;

import android.graphics.Color;

/**
 * Created by ambient on 7/27/16.
 */
public class AppSettings {

    public static class Settings {
        public String fontFamily;//Exact name
        public int fontSize;
        public String fontColor;//Hexa RGB

        public Settings() {
            fontFamily = "themify";
            fontSize = 16;
            fontColor = "#FF000000";
        }

        public int getColor() {
            try {
                String color = fontColor;
                if (color.indexOf("#") != 0) {
                    color = "#" + fontColor;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public String getFont() {
            return "fonts/" + fontFamily + ".ttf";
        }
    }

    Settings defaultSettings;
    Settings title;
    Menu menu;
    Top top;
    //...


    class Menu {
        Settings header;
        Settings item;
    }

    class Top {
        Settings header;
        Settings item;
    }
    //...
}