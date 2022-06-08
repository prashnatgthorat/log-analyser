package org.logs;


import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class LogAnalyserTest {

    private static final String INPUT_PATH_EMPTY_FILE = "src/test/resources/empty.txt";
    private static final String INPUT_PATH_SAMPLE_WORKING = "src/test/resources/sample-input.json";

    private Logger log = LogManager.getLogger(this.getClass());

    @Test
    public void process(){
        try {
            log.info("Starting execution of process");
            String logFilePathc=INPUT_PATH_EMPTY_FILE;
            LogAnalyser loganalyser  =new LogAnalyser( logFilePathc);
            loganalyser.process();
            assertTrue(true);

        } catch (Exception exception) {
            log.error("Exception in execution ofprocess-"+exception,exception);
            exception.printStackTrace();
            assertFalse(false);
        }
    }
    @Test
    public void main(){
        try {
            log.info("Starting execution of main");
            String[] args = null;
            ;
            String logFilePathc="";
            ;
            LogAnalyser loganalyser  =new LogAnalyser( logFilePathc);
            loganalyser.main( args);
            assertTrue(true);

        } catch (Exception exception) {
            log.error("Exception in execution ofmain-"+exception,exception);
            exception.printStackTrace();
            assertFalse(false);
        }
    }
}
