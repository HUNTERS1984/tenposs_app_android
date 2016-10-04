package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/5/16.
 */
public class TopInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return app_id + "" + time + "" + privateKey;
        }
    }

    public static class Response extends CommonResponse {
        public class ResponseData implements Serializable {

            public ListItem items;
            public ListPhoto photos;
            public ListNews news;
            public ListImage images;
            public ListContact contact;
        }

        public ResponseData data;
    }

    public class ListTopObject implements Serializable {
        public int top_id;
    }

    public class ListItem extends ListTopObject {
        public ArrayList<ItemsInfo.Item> data;

        public int size() {
            return data == null ? 0 : data.size();
        }

    }

    public class ListPhoto extends ListTopObject {
        public ArrayList<PhotoInfo.Photo> data;

        public int size() {
            return data == null ? 0 : data.size();
        }
    }

    public class ListNews extends ListTopObject {
        public ArrayList<NewsInfo.News> data;

        public int size() {
            return data == null ? 0 : data.size();
        }
    }

    public class ListImage extends ListTopObject {
        public ArrayList<Image> data;

        public int size() {
            return data == null ? 0 : data.size();
        }
    }

    public class ListContact extends ListTopObject {
        public ArrayList<Contact> data;

        public int size() {
            return data == null ? 0 : data.size();
        }
    }

    public static class Image extends UrlImageObject implements Serializable {

        public Image(String image) {
            image_url = image;
        }

        String image_url;

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }
    }

    public class Contact implements Serializable {
        public int id;
        String latitude;
        String longitude;
        public String tel;
        public String title;
        public String start_time;
        public String end_time;

        public String getLocation() {
            return latitude + "," + longitude;
        }
    }
}