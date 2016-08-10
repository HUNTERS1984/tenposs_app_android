package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 8/5/16.
 */
public class UserInfoCommunicator extends TenpossCommunicator {
    public UserInfoCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
