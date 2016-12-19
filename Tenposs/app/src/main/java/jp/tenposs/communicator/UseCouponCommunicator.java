package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 12/12/16.
 */

public class UseCouponCommunicator extends TenpossCommunicator {
    public UseCouponCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
