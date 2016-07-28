package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 7/26/16.
 */
public class LoginCommunicator extends TenpossCommunicator {
    public LoginCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
