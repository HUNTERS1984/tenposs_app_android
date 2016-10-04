package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

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
        String category = "";

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }

        public String getCreatedDate() {
            return date;
        }

        public String getCategory() {
            if (this.category != null) {
                return this.category;
            } else {
                return "カテゴリー";
            }
        }
    }

    public class Message implements Serializable {
        public String message;
    }
}