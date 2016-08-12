package jp.tenposs.datamodel;

import java.io.Serializable;

/**
 * Created by ambient on 8/12/16.
 */
public class ReserveInfo {
    public static class Request extends CommonRequest {
        public int store_id;

        @Override
        String sigInput() {
            return store_id + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {
        public Response.ResponseData data;

        public class ResponseData {
            public Reserve reserve;

            public class Reserve implements Serializable {
                public int id;
                public String reserve_url;
                public int store_id;
                public String created_at;
                public String updated_at;
                public String deleted_at;
            }
        }
    }

}
