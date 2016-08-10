package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 8/8/16.
 */
public class MenuInfoCommunicator extends TenpossCommunicator {
    public MenuInfoCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
