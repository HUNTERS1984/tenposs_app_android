package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/10/16.
 */
public class NewsInfo {
    public static class Request extends CommonRequest {
        public int store_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return store_id + "" + pageindex + "" + pagesize + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {

            public class News implements Serializable {
                public int id;
                public String title;
                public String description;
                public String date;
                public int store_id;
                public String image_url;
            }

            public ArrayList<News> news;
        }

        public class Message implements Serializable {
            public String message;
        }

        public ResponseData data;
        ArrayList<Message> notification;
        public int totalnew;
    }
}