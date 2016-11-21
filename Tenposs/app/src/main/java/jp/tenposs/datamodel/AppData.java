package jp.tenposs.datamodel;

/**
 * Created by ambient on 11/1/16.
 */

public class AppData {
    public AppInfo.Response mAppInfo;
    public int mStoreId;

    static AppData appData = null;

    private AppData() {
        this.mAppInfo = null;
        this.mStoreId = 0;
    }

    public static AppData sharedInstance() {
        if (appData == null) {
            appData = new AppData();
        }
        return appData;
    }

    public enum TemplateId {
        CommonTemplate,
        RestaurantTemplate,
        FashionTemplate,

    }

    public TemplateId getTemplate() {
        //TODO: return Restaurant
//        return TemplateId.RestaurantTemplate;
        return TemplateId.CommonTemplate;
        /*if (this.mAppInfo == null) {
            return TemplateId.CommonTemplate;
        } else {
            if (this.mAppInfo.data.app_setting.template_id == 1) {
                return TemplateId.CommonTemplate;

            } else if (this.mAppInfo.data.app_setting.template_id == 2) {
                return TemplateId.RestaurantTemplate;

            } else if (this.mAppInfo.data.app_setting.template_id == 3) {
                return TemplateId.FashionTemplate;
            }
        }
        return TemplateId.CommonTemplate;*/
    }
}
