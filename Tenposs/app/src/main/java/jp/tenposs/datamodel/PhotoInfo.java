package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/20/16.
 */
public class PhotoInfo {
    public static class Request extends CommonRequest {
        public int category_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + category_id + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public ResponeData data;

        public class ResponeData implements Serializable {
            public ArrayList<Photo> photos;
            public int total_photos;
        }
    }

    public class Photo extends UrlImageObject implements Serializable {
        public int id;
        public String image_url;
        public int photo_category_id;
        public String updated_at;
        public String created_at;

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }
    }
}

