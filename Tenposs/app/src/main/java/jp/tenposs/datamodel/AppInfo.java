package jp.tenposs.datamodel;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/5/16.
 */
public class AppInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return app_id + "" + time + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public ResponseData data;

        public class ResponseData implements Serializable {
            public int id;
            public String name;
            public String description;
            public String created_time;
            public int status;
            public String created_at;
            public String updated_at;
            public String deleted_at;
            public int user_id;

            public AppSetting app_setting;

            public TopComponent getTopComponent(int id) {
                for (TopComponent component : top_components) {
                    if (component.id == id) {
                        return component;
                    }
                }
                return null;
            }

            public SideMenu getSideMenu(int id) {
                for (SideMenu menu: side_menu) {
                    if (menu.id == id) {
                        return menu;
                    }
                }
                return null;
            }

            public ArrayList<TopComponent> top_components;


            public ArrayList<SideMenu> side_menu;


            public ArrayList<Store> stores;


        }
    }

    public class TopComponent implements Serializable {
        public int id;
        public String name;
        public Pivot pivot;

        public class Pivot implements Serializable {
            public int app_setting_id;
            public int component_id;
        }
    }

    public static class SideMenu implements Serializable {
        public int id;
        public String name;
        public Pivot pivot;
        public String icon = "";

        public SideMenu(int settingScreen, String settings) {
            id = settingScreen;
            name = settings;
        }

        public class Pivot implements Serializable {
            public int app_setting_id;
            public int sidemenu_id;
        }
    }

    public class Store implements Serializable {
        public int id;
        public String name;
        public int app_id;
    }

    public class AppSetting implements Serializable {
        public int id;
        public int app_id;
        public String title;
        public String title_color;
        public int font_size;
        public String font_family;
        public String header_color;
        public String menu_icon_color;
        public String menu_background_color;
        public String menu_font_color;
        public int menu_font_size;
        public String menu_font_family;
        public int template_id;

        public int getToolbarIconColor() {
            try {
                String color = title_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + title_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public int getToolbarTitleColor() {
            try {
                String color = title_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + title_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public int getMenuBackgroundColor() {
            try {
                String color = menu_background_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + menu_background_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public int getMenuIconColor() {
            try {
                String color = menu_icon_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + menu_icon_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public int getMenuTitleColor() {
            try {
                String color = menu_font_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + menu_font_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public String getToolBarTitleFont() {
            return "fonts/" + font_family + ".ttf";
        }
    }
}
