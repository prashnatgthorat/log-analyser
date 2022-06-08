package org.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logs.db.DBManager;
import org.logs.model.Event;
import org.logs.model.LogEvent;

import java.util.concurrent.BlockingQueue;

public class LogEventWriter implements Runnable{

    Logger logger = LogManager.getLogger(getClass());
    private BlockingQueue<Event> queue ;
    private DBManager dbmanager;
    public LogEventWriter(DBManager db, BlockingQueue<Event> storeQueue){
        this.queue = storeQueue;
        dbmanager = db;
    }

    @Override
    public void run() {
        logger.info("LogEventWriter Start");
        boolean flag = true;
        while (flag) {
            try {
                Event event = queue.take();
                if(event.getEventType().equalsIgnoreCase("EOF")){
                    flag =false;
                }else{
                    LogEvent logEvent = (LogEvent)event;
                    logger.info("LogEventWriter "+logEvent);
                    dbmanager.insertEvent(logEvent);
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        logger.info("LogEventWriter End");
    }
}
