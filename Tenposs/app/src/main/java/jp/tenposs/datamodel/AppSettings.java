package jp.tenposs.datamodel;

/**
 * Created by ambient on 7/27/16.
 */
public class AppSettings {

    public class Settings {
        String fontFamily;//Exact name
        int fontSize;
        String fontColor;//Hexa RGB
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