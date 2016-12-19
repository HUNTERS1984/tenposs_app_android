package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 12/1/16.
 */

public class CouponDetailCommunicator extends TenpossCommunicator {
    public CouponDetailCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        return false;
    }
}
