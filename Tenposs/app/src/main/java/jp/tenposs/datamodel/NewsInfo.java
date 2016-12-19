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

        public int category_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + category_id + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
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

    public class News extends CommonItem implements Serializable {
        public int id;
        String title;
        String description;
        public String date;
        public int store_id;
        public int new_category_id;

        String image_url;
        String category = "";

        String deleted_at;
        String updated_at;
        String created_at;

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }

        public String getCreatedDate() {
            return date;
        }

        public String getLastModifyDate() {
            if (updated_at != null) {
                return updated_at;
            } else {
                return created_at;
            }
        }

        public void setCategory(ArrayList<NewsCategoryInfo.Category> categories) {
            for (NewsCategoryInfo.Category category : categories) {
                if (category.id == this.new_category_id) {
                    this.category = category.name;
                    break;
                }
            }
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategory() {
            if (this.category != null) {
                return this.category;
            } else {
                return "カテゴリー";
            }
        }

        @Override
        public String getTitle() {
            if (title != null) {
                return title;
            } else {
                return "";
            }
        }

        @Override
        public String getDescription() {
            if (description != null) {
                description = description.replaceAll("\r\n", "\n");
                description = description.replaceAll("\n\r", "\n");
                return description;
            } else {
                return "";
            }
        }

        @Override
        public String getPrice() {
            return null;
        }
    }

    public class Message implements Serializable {
        public String message;
    }
}