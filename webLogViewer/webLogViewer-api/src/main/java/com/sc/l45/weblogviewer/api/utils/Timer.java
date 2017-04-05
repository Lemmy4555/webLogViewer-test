package com.sc.l45.weblogviewer.api.utils;

import java.util.concurrent.TimeUnit;

public class Timer {
	private long currentMs;
	
	public Timer() {
		reset();
	}
	
	public void reset() {
		currentMs = System.currentTimeMillis();
	}
	
	public String time() {
		long millis = System.currentTimeMillis() - currentMs;
		return toString(millis);
	}
	
	public long timeInMillis() {
        return System.currentTimeMillis() - currentMs;
    }
	
	public String toString(long millis) {
	    long m = TimeUnit.MILLISECONDS.toMinutes(millis);
        long s = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(m);
        long ms = millis - TimeUnit.MINUTES.toMillis(m) - TimeUnit.SECONDS.toMillis(s);
        reset();
        if(m > 0) {
            return String.format("%dm %ds %dms", m, s, ms);
        } else if (s > 0) {
            return String.format("%ds %dms", s, ms);
        } else {
            return String.format("%dms", ms);
        }
	}
}
