package org.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logs.model.Event;
import org.logs.model.FileLogEvent;
import org.logs.model.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class LogEventProcessor implements Runnable{

    Logger logger = LogManager.getLogger(getClass());
    private BlockingQueue<Event> inboundQueue ;
    private BlockingQueue<Event> outQueue ;

    private Map<String, List<FileLogEvent>> eventMap = new HashMap<>();
    public LogEventProcessor(BlockingQueue<Event> inboundQueue, BlockingQueue<Event> outQueue){
    this.inboundQueue = inboundQueue;
    this.outQueue = outQueue;
    }

    @Override
    public void run() {

        logger.info("LogEventProcessor Start");
        boolean flag = true;
        while (flag) {
            try {
                Event event = inboundQueue.take();
                if(event.getEventType().equalsIgnoreCase("EOF")){
                    outQueue.put(event);
                    flag = false;
                }else {

                    FileLogEvent logEvent = (FileLogEvent) event;
                    String id = logEvent.getId();

                    if (eventMap.containsKey(id)) {
                        List<FileLogEvent> eventList = eventMap.get(id);
                        eventList.add(logEvent);
                        FileLogEvent startEvent = null;
                        FileLogEvent finishEvent = null;
                        for (FileLogEvent flevent : eventList) {
                            if (flevent.getState().equalsIgnoreCase("STARTED")) {
                                startEvent = flevent;
                            } else if (flevent.getState().equalsIgnoreCase("FINISHED")) {
                                finishEvent = flevent;
                            }
                        }

                        if (startEvent != null && finishEvent != null) {
                            long duration = finishEvent.getTimestamp() - startEvent.getTimestamp();
                            if (duration >= LogAnalyser.FLAG_EVENT_THRESHOLD_MS) {
                                LogEvent e = new LogEvent(finishEvent.getId(), duration, finishEvent.getType(), finishEvent.getHost());
                                logger.info("LogEventProcessor "+e);
                                outQueue.put(e);
                            }
                        }

                    } else {
                        List<FileLogEvent> eventList = new ArrayList<>();
                        eventList.add(logEvent);
                        eventMap.put(id, eventList);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("LogEventProcessor End");
    }
}
