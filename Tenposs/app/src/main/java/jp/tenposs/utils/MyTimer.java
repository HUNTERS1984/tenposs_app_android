package jp.tenposs.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ambient on 7/7/16.
 */
public class MyTimer {

    int seconds;
    boolean repeat;
    MyTimerFireListener listener;
    Timer timer;

    boolean isValid;
    boolean isScheduled;
    long timeLeft;
    long fireTime;

    protected MyTimer() {

    }

    protected MyTimer(int seconds, boolean repeat, MyTimerFireListener listener) {
        this.timer = new Timer();
        this.seconds = seconds;
        this.repeat = repeat;
        this.listener = listener;
    }

    public static MyTimer timerWithTimeInterval(int seconds, boolean repeat, MyTimerFireListener listener) {
        MyTimer newTimer = new MyTimer(seconds, repeat, listener);
        return newTimer;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void invalidate() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.isValid = false;
        this.timer = null;
    }

    public boolean isScheduled() {
        return this.isScheduled;
    }

    public boolean scheduleNow(long delayMillis) {
        long currentTime = System.currentTimeMillis();
        this.fireTime = currentTime + delayMillis;
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerFired();
            }
        }, delayMillis);
        this.isValid = true;
        return true;
    }

    public boolean pause() {
        if (isValid()) {
            try {
                long currentime = System.currentTimeMillis();
                this.timeLeft = this.fireTime - currentime;
                this.timer.cancel();
                this.timer = null;
            } catch (Exception ex) {

            }
        }
        return false;
    }

    public boolean resume() {
        if (this.timeLeft > 0) {
            scheduleNow(this.timeLeft);
        }
        return true;
    }

    public int initialTimeInterval() {
        return 0;
    }

    void timerFired() {
        this.isValid = false;
        this.timer = null;
        if (this.listener != null) {
            this.listener.timerHasFire(this);
        }
        if (this.repeat == true) {
            scheduleNow(this.seconds * 1000);
        }
    }
}
