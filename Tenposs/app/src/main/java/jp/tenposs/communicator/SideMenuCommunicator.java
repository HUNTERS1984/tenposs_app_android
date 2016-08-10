package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 8/8/16.
 */
public class SideMenuCommunicator extends TenpossCommunicator{
    public SideMenuCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
