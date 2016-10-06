package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 10/5/16.
 */

public class NewsCategoryInfo {
    public static class Request extends CommonRequest {

        public int store_id;

        @Override
        String sigInput() {
            return store_id + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {

        public ResponseData data;

        public class ResponseData implements Serializable {
            public ArrayList<Category> news_categories;
        }
    }

    public class Category implements Serializable {
        public int id;
        public String name;
    }
}
