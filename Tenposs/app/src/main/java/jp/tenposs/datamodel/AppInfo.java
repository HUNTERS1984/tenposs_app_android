package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/5/16.
 */
public class AppInfo {
    public class Request extends CommonRequest {

    }

    public class Response extends CommonResponse {
        public ResponseData get(int storeId) {
            for (ResponseData responseData : this.data) {
                if (responseData.id == storeId) {
                    return responseData;
                }
            }
            return null;
        }

        public class ResponseData implements Serializable {
            public class Info implements Serializable {
                public String latitude;//							string
                public String longitude;//							string
                public String tel;//							string
                public String title;//							string
                public String start_time;//							timestamp
                public String end_time;//							timestamp
            }

            public class Setting implements Serializable {
                public String title;
                public String title_color;          //string
                public int font_size;               //int
                public String font_family;          //string
                public String header_color;         //string
                public String menu_icon_color;      //string
                public String menu_background_color;//string
                public String menu_font_color;      //string
                public int menu_font_size;          //int
                public String menu_font_family;     //string
                public int template_id;             //int
                public String top_main_image_url;   //string
            }

            public class Menu implements Serializable {
                public int id;//							int
                public String name;//							string
            }

            public int id;                                //integer			id of store
            public String name;//								string
            public Info info;//								json
            public Setting setting;//								array
            public ArrayList<Menu> menus;
        }

        public ArrayList<ResponseData> data;

    }
}
