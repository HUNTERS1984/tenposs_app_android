package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 9/20/16.
 */

public class ItemDetailInfo {
    public static class Request extends CommonRequest {
        public int item_id;

        @Override
        String sigInput() {
            return token + "" + item_id + privateKey + "" + time;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData {
            public ItemsInfo.Item items;
            public ArrayList<ItemsInfo.Item> items_related;
            public int total_items_related;
        }

        public ResponseData data;
    }
}
