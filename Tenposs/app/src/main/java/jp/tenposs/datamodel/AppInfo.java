package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/5/16.
 */
public class AppInfo {
    public static class Request extends CommonRequest {
        public int app_id;

        @Override
        String sigInput() {
            return privateKey + "" + time;
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
            }

            public ArrayList<TopComponent> top_components;

            public class TopComponent implements Serializable {
                public int id;
                public String name;
                public Pivot pivot;

                public class Pivot implements Serializable {
                    public int app_setting_id;
                    public int component_id;
                }
            }

            public ArrayList<SideMenu> side_menu;

            public class SideMenu implements Serializable {
                public int id;
                public String name;
                public Pivot pivot;

                public class Pivot implements Serializable {
                    public int app_setting_id;
                    public int sidemenu_id;
                }
            }

            public ArrayList<Store> stores;

            public class Store implements Serializable {
                public int id;
                public String name;
                public int app_id;
            }
        }
    }
}
