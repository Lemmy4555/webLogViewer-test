package com.sc.l45.weblogviewer.test.config;

import java.io.File;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.utils.Timer;

/**
 * Basic configuration for JUnit tests
 * 
 * @author Lemmy4555
 */
public class TestConf {
	/**
	 * This subclass contains informations about the test file
	 */
    public final class TEST_FILE {
        public final static String NAME = "Test.log";
    }

    /**
     * Log JUnit tests uncaught exceptions
     */
    private final static Logger logger = LoggerFactory.getLogger(TestConf.class);
    /**
     * Log JUnit tests timings
     */
    private final static Logger loggerJunit = LoggerFactory.getLogger("junit-tests");

    public File testFile;

    {
    	/* Create an instance of the testFile, but in case can't read it I want an error only if
    	 * if i try to use it in a JUnit test, the priority is to run the Arquillian istance and let test run */
        try {
            testFile = new File(TestConf.class.getClassLoader().getResource("Test.log").toURI());
        } catch (Exception e) {
        }
    }

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        Timer timer;
        
        /**
         * I want to log exceptions in console when running a test method with JUnit
         */
        protected void failed(Throwable e, Description description) {
            logger.error("{} failed ", description.getDisplayName(), e);
            super.failed(e, description);
        }
        
        /**
         * Reset timer to log time passed on test methods end
         */
        protected void starting(Description description) {
            if(timer == null) {
                timer = new Timer();
            } else {
                timer.reset();
            }
            loggerJunit.info("{} started", description.getDisplayName());
            super.starting(description);
        }
        
        /**
         * Log time needed to end the test method
         */
        protected void finished(Description description) {
            loggerJunit.info("{} finished in {}", description.getDisplayName(), timer.time());
            super.finished(description);
        }

    };

}
