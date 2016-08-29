package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/11/16.
 */
public class MenuInfo {
    public static class Request extends CommonRequest {
        public int store_id;

        @Override
        String sigInput() {
            //sha256(app_id + time  + store_id + secret_key)
            return app_id + "" + time + "" + store_id + "" + privateKey;
//            return store_id + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {
        public ResponseData data;

        public class ResponseData implements Serializable {
            public ArrayList<Menu> menus;


        }
    }

    public class Menu implements Serializable {
        public int id;
        public String name;
    }
}
