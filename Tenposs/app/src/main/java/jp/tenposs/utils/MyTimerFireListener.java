package jp.tenposs.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by ambient on 10/17/16.
 */

public abstract class MyTimerFireListener {

    final Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            MyTimer timer = (MyTimer) msg.obj;
            timerFire(timer);
            return false;
        }
    });

    public void timerHasFire(MyTimer timer) {
        Message msg = h.obtainMessage();
        msg.obj = timer;
        h.sendMessage(msg);
    }

    protected abstract void timerFire(MyTimer timer);
}
