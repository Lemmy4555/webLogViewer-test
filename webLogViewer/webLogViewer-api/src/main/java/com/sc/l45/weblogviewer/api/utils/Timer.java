package com.sc.l45.weblogviewer.api.utils;

import java.util.concurrent.TimeUnit;

/**
 * Timer used in tests to get requests timings in 00s 00m 000ms format
 * 
 * @author Lemmy4555
 */
public class Timer {
	private long currentMs;
	
	/**
	 * Since the timer is created it start running
	 */
	public Timer() {
		reset();
	}
	
	/**
	 * Reset timer to {@link System#currentTimeMillis()}
	 */
	public void reset() {
		currentMs = System.currentTimeMillis();
	}
	
	/**
	 * Get time in a formatted String using this format: 00m 00s 000ms (minutes, seconds and milliseconds)
	 * @return time passed since last reset as String
	 */
	public String time() {
		long millis = System.currentTimeMillis() - currentMs;
		return toString(millis);
	}
	
	/**
	 * Get time in milliseconds
	 * @return time in milliseconds as long
	 */
	public long timeInMillis() {
        return System.currentTimeMillis() - currentMs;
    }
	
	/**
	 * Convert a number in a formatted String using this format: 00m 00s 000ms (minutes, seconds and milliseconds)
	 * @param millis
	 * @return time as a formatted string: 00m 00s 000ms
	 */
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
