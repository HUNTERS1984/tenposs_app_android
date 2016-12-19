package jp.tenposs.datamodel;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/5/16.
 */
public class AppInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return app_id + "" + time + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {
        ApplicationInfo data;

        public String getAppName() {
            if (data != null && data.name != null)
                return data.name;
            else return "";
        }

        public ArrayList<TopComponent> getTopComponents() {
            if (data != null && data.top_components != null) {
                return data.top_components;
            } else {
                return new ArrayList<>();
            }
        }

        public AppSetting getAppSetting() {
            if (data != null && data.app_setting != null) {
                return data.app_setting;
            } else {
                return new AppSetting();
            }
        }

        public ArrayList<SideMenu> getSideMenu() {
            if (data != null && data.side_menu != null) {
                return data.side_menu;
            } else {
                return new ArrayList<>();
            }
        }

        public ApplicationInfo getAppInfo() {
            return data;
        }

        public SideMenu getSideMenuById(int id) {
            if (data != null && data.side_menu != null) {
                for (SideMenu menu : data.side_menu) {
                    if (menu.id == id) {
                        return menu;
                    }
                }
                return null;
            }
            return null;
        }

        public ArrayList<Store> getStores() {
            if (data != null && data.stores != null)
                return data.stores;
            else
                return new ArrayList<>();
        }

        public class ApplicationInfo implements Serializable {
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

            public SideMenu getSideMenuAtIndex(int index) {
                if (index >= 0 && side_menu.size() > index) {
                    return side_menu.get(index);
                } else {
                    return null;
                }
            }

            public ArrayList<TopComponent> top_components;

            public ArrayList<SideMenu> side_menu;

            public ArrayList<Store> stores;
        }
    }

    public class TopComponent implements Serializable {
        public int id;
        public String name;
        String viewmore;

        public boolean showViewMore() {
            int more = Utils.atoi(viewmore);
            return more != 0;
        }
    }

    public static class SideMenu implements Serializable {
        public int id;
        public String name;
        public String icon = "";
        public int fontType = 0;

        public SideMenu(int settingScreen, String settings, String icon) {
            id = settingScreen;
            name = settings;
            this.icon = icon;
        }
    }

    public class Store implements Serializable {
        public int id;
        public String name;
        public int app_id;
    }

    public class AppSetting implements Serializable {
        int id;
        int app_id;
        String title;

        String title_color;
        String font_size;
        String font_family;
        String header_color;

        String menu_icon_color;
        String menu_background_color;
        String menu_font_color;
        String menu_font_size;
        String menu_font_family;
        int template_id;

        public String company_info;
        public String user_privacy;

        public AppSetting() {
            id = 1;
            app_id = 1;
            title = "Application Title";
            title_color = "000000";
            font_size = "small";
            font_family = "roboto light";
            header_color = "FFFFFF";
            menu_icon_color = "FFFFFF";
            menu_background_color = "000000";
            menu_font_color = "FFFFFF";
            menu_font_size = "medium";
            menu_font_family = "Arial";
            template_id = 1;
            company_info = "";
            user_privacy = "";
        }

        public int getToolbarIconColor() {
            return Utils.colorFromHex(this.menu_icon_color, Color.WHITE);
        }

        public int getToolbarTitleColor() {
            return Utils.colorFromHex(this.title_color, Color.BLACK);
        }

        public int getToolbarBackgroundColor() {
            return Utils.colorFromHex(this.header_color, Color.WHITE);
        }

        public int getMenuBackgroundColor() {
            return Utils.colorFromHex(this.menu_background_color, Color.GRAY);
        }


        public String getMenuItemTitleFontSize() {
            try {
                return this.menu_font_size;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        public String getToolbarTitleFontSize() {
            try {
                return this.font_size;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        public int getMenuItemTitleColor() {
            try {
                String color = this.menu_font_color;
                if (color.indexOf("#") != 0) {
                    color = "#" + this.menu_font_color;
                }
                return Color.parseColor(color);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Color.BLACK;
        }

        public String getMenuItemTitleFont() {
            if (this.menu_font_family != null)
                return this.menu_font_family;
            return "Arial";
        }

        public String getToolBarTitleFont() {
            if (this.font_family != null)
                return this.font_family;
            return "Arial";
        }
    }
}
