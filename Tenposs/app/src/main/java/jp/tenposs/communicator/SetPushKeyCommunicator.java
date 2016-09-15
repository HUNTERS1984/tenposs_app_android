package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 9/15/16.
 */

public class SetPushKeyCommunicator extends TenpossCommunicator {
    public SetPushKeyCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
