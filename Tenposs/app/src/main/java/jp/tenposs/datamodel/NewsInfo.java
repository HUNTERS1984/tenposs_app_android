package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import jp.tenposs.communicator.TenpossCommunicator;

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
            return app_id + "" + time + "" + store_id + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {
            public ArrayList<News> news;
        }

        public ResponseData data;
        ArrayList<Message> notification;
        public int totalnew;

        public News getItemById(int id) {
            try {
                for (News news : data.news) {
                    if (news.id == id) {
                        return news;
                    }
                }
                return null;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public class News extends UrlImageObject implements Serializable {
        public int id;
        public String title;
        public String description;
        public String date;
        public int store_id;
        String image_url;
        public String category = "";

        @Override
        public String getImageUrl() {
            String temp = image_url.toLowerCase(Locale.US);
            if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {
                return image_url;
            } else {
                return TenpossCommunicator.BASE_ADDRESS + image_url;
            }
        }

        public String getCreatedDate() {
            return date;
        }
    }

    public class Message implements Serializable {
        public String message;
    }
}