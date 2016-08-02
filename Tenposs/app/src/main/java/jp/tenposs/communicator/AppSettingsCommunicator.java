package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 7/29/16.
 */
public class AppSettingsCommunicator extends TenpossCommunicator {
    public AppSettingsCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
