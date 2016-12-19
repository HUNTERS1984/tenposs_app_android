package jp.tenposs.datamodel;

/**
 * Created by ambient on 8/5/16.
 */
public enum ScreenDataStatus {
    ScreenDataStatusUnload,
    ScreenDataStatusLoading,
    ScreenDataStatusLoaded;

    public static ScreenDataStatus fromInt(int item) {
        for (ScreenDataStatus type : ScreenDataStatus.values()) {
            if (type.ordinal() == item) {
                return type;
            }
        }
        return ScreenDataStatusUnload;
    }
}
