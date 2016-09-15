package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 9/12/16.
 */

public class StaffCategoryInfo {

    public static class Request extends CommonRequest {

        public int store_id;

        @Override
        String sigInput() {
            //TODO:
            return "";
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {
            public ArrayList<StaffCategory> staff_categories;
        }

        public ResponseData data;
    }

    public class StaffCategory {
        public int id;
        public String name;
    }
}
