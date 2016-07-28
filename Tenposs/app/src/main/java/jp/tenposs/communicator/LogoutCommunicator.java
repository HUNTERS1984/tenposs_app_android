package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 7/26/16.
 */
public class LogoutCommunicator extends TenpossCommunicator {
    public LogoutCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
