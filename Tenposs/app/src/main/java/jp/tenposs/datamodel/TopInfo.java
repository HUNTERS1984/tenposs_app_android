package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/5/16.
 */
public class TopInfo {
    public class Request extends CommonRequest {
        public String token;    //string			o	token return when login
        public long time;       //long			o	Unix Timestamp miliseconds
        public String sig;      //string			o	sha256( token + private key + time)
    }

    public static class Response extends CommonResponse {
        public ResponseData get(int storeId) {
            for (ResponseData responseData : this.data) {
                if (responseData.store_id == storeId) {
                    return responseData;
                }
            }
            return null;
        }

        public class ResponseData implements Serializable {

            public class Item implements Serializable {
                public int id;// int

                public String title;// string
                public String price;// string
                public String image_url;// string
                public String description;// string
            }

            public class Photo implements Serializable {
                public int id;// int

                public int categoryid;// int
                public String categoryname;// string
                public String image_url;// string

            }

            public class News implements Serializable {
                public int id;// int

                public String title;// string
                public String description;// string
                public String date;// datetime
                public String image_url;// string

            }

            public class Image implements Serializable {
                public String image_url;// string

                public Image(String url) {
                    image_url = url;
                }
            }

            public int store_id;
            public ArrayList<Item> items;//								array			list items
            public ArrayList<Photo> photos;//								array			list photo
            public ArrayList<News> news;//								integer			list news
            public ArrayList<Image> images;//								array

        }

        public ArrayList<ResponseData> data;
    }
}