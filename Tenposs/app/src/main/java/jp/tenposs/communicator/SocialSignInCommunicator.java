package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 8/21/16.
 */
public class SocialSignInCommunicator extends TenpossCommunicator {
    public SocialSignInCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
