package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ambient on 8/5/16.
 */
public class SideMenuInfo {
    public class Request extends CommonRequest {
        public String token;   //string			o	token return when login
        public long time;    //long			o	Unix Timestamp miliseconds
        public String sig;     //string			o	sha256( token + private key + time)
    }

    public static class Response extends CommonResponse {

        public ResponseData data;//									json

        public static class ResponseData implements Serializable {
            public ArrayList<Menu> menus;// array list items

            public static class Menu implements Serializable {
                public int id;// int
                public String name;// string
                public String icon;

                public Menu(int id, String name, String icon) {
                    this.id = id;
                    this.name = name;
                    this.icon = icon;
                }
            }

            public ResponseData() {
                menus = new ArrayList<>();
            }
        }

        public Response() {
            data = new ResponseData();
        }
    }

    public static Response fakeData() {
        Response response = new Response();
        response.data.menus.add(new Response.ResponseData.Menu(0, "Home", "ti-home"));
        response.data.menus.add(new Response.ResponseData.Menu(1, "Menu", "ti-menu-alt"));
        response.data.menus.add(new Response.ResponseData.Menu(2, "Reserve", "ti-calendar"));
        response.data.menus.add(new Response.ResponseData.Menu(3, "News", ""));
        response.data.menus.add(new Response.ResponseData.Menu(4, "Photo Gallery", "ti-image"));
        response.data.menus.add(new Response.ResponseData.Menu(5, "Coupon", "ti-ticket"));
        response.data.menus.add(new Response.ResponseData.Menu(6, "Chat", "ti-comment-alt"));
        response.data.menus.add(new Response.ResponseData.Menu(7, "Setting", "ti-settings"));
        return response;
    }
}
