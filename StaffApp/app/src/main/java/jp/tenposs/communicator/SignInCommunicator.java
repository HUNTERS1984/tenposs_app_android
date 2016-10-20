package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 10/17/16.
 */

public class SignInCommunicator extends TenpossCommunicator {
    public SignInCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
