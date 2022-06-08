package org.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logs.db.DBManager;
import org.logs.model.Event;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LogAnalyser {

    Logger logger = LogManager.getLogger(getClass());
    public static final int FLAG_EVENT_THRESHOLD_MS = 4;
    private String logFilePath;

    public LogAnalyser(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void setLogFilePath(String logFilePath){
        this.logFilePath = logFilePath;
    }



    public void process() throws Exception {
        logger.info("LogAnalyser Process Start");

        if(logFilePath ==null || logFilePath.length() ==0){
            throw new Exception("FileNotFound");
        }
        BlockingQueue<Event> readerQueue = new ArrayBlockingQueue<>(100);
        LogEventReader reader = new LogEventReader(logFilePath, readerQueue);

        BlockingQueue<Event> processorQueue = new ArrayBlockingQueue<>(100);
        LogEventProcessor processor =  new LogEventProcessor(readerQueue, processorQueue);

        DBManager dbmanager = new DBManager();
        dbmanager.startHSQLDB();
        dbmanager.createHSQLDBTable();

        LogEventWriter writer = new LogEventWriter(dbmanager, processorQueue);

        Thread t1 = new Thread(reader);
        Thread t2 = new Thread(processor);
        Thread t3 = new Thread(writer);
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        }catch (InterruptedException e){
            System.exit(0);
        }
        dbmanager.stopHSQLDB();
        logger.info("LogAnalyser Process End");
        System.exit(0);
    }

    public static void main(String[] args) {

        String inputFilePath="logfile.txt";

        LogAnalyser logAnalyser = new LogAnalyser(inputFilePath);
         if (args != null && args.length == 1) {
            inputFilePath=args[0];
        }

        logAnalyser.setLogFilePath(inputFilePath);

        try {
            logAnalyser.process();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
