package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 7/26/16.
 */
public class SignOutCommunicator extends TenpossCommunicator {
    public SignOutCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
