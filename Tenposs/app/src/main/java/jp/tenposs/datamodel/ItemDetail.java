package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 9/20/16.
 */

public class ItemDetail {
    public static class Request extends CommonRequest {
        public int item_id;

        @Override
        String sigInput() {
            return token + "" + item_id + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData {
            public ItemsInfo.Item detail;
            public ArrayList<ItemsInfo.Item> items;
        }

        public ResponseData data;
    }
}
