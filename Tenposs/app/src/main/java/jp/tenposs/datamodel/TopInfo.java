package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/5/16.
 */
public class TopInfo {
    public static class Request extends CommonRequest {
        public int app_id;

        @Override
        String sigInput() {
            return privateKey + "" + time;
        }
    }

    public static class Response extends CommonResponse {
        public class ResponseData implements Serializable {

            public ArrayList<Item> items;

            public class Item implements Serializable {
                public int id;

                public String title;
                public String price;
                public String image_url;
                public String description;
            }

            public ArrayList<Photo> photos;

            public class Photo implements Serializable {
                public int id;// int
                public String image_url;
                public int photo_category_id;
                public String created_at;
                public String updated_at;
                public String deleted_at;
            }

            public ArrayList<News> news;

            public class News implements Serializable {
                public int id;
                public int category_id;
                public String category_name;
                public String title;
                public String description;
                public String date;
                public int store_id;
                public String image_url;
            }

            public ArrayList<Image> images;//								array

            public class Image implements Serializable {
                public String image_url;
            }

            public ArrayList<Address> addresses;

            public class Address implements Serializable {
                public String latitude;
                public String logitude;
            }
        }

        public ResponseData data;
    }
}