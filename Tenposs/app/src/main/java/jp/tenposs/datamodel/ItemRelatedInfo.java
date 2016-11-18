package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 9/20/16.
 */

public class ItemRelatedInfo {
    public static class Request extends CommonRequest {
        public int item_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return token + "" + item_id + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData {
            public ArrayList<ItemsInfo.Item> items;
            public int total_items;
        }

        public ResponseData data;
    }
}
