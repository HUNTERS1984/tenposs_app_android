package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/20/16.
 */
public class PhotoCategoryInfo {
    public static class Request extends CommonRequest {
        public int store_id;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + store_id + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public ResponseData data;

        public class ResponseData implements Serializable {
            public ArrayList<PhotoCat> photo_categories;

        }
    }

    public class PhotoCat implements Serializable {
        public int id;
        public String name;
    }
}
