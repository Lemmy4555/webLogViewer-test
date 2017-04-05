package com.sc.l45.weblogviewer.test.config;

import java.io.File;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.utils.Timer;


public class TestConf {
    public final class TEST_FILE {
        public final static String NAME = "Test.log";
    }

    private final static Logger logger = LoggerFactory.getLogger(TestConf.class);
    private final static Logger loggerJunit = LoggerFactory.getLogger("junit-tests");

    public File testFile;

    {
        try {
            testFile = new File(TestConf.class.getClassLoader().getResource("Test.log").toURI());
        } catch (Exception e) {
        }
    }

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        Timer timer;
        
        protected void failed(Throwable e, Description description) {
            logger.error("{} failed ", description.getDisplayName(), e);
            super.failed(e, description);
        }
        
        protected void starting(Description description) {
            if(timer == null) {
                timer = new Timer();
            } else {
                timer.reset();
            }
            loggerJunit.info("{} started", description.getDisplayName());
            super.starting(description);
        }
        
        protected void finished(Description description) {
            loggerJunit.info("{} finished in {}", description.getDisplayName(), timer.time());
            super.finished(description);
        }

    };

}
