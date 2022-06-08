package org.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.logs.model.EOFEvent;
import org.logs.model.Event;
import org.logs.model.FileLogEvent;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class LogEventReader implements Runnable {

    Logger logger = LogManager.getLogger(getClass());

    private BlockingQueue<Event> queue ;
    private File file;

    public LogEventReader(String inputPath, BlockingQueue<Event> queue){
        this.queue = queue;
        this.file = new File(inputPath);
    }

    @Override
    public void run() {
        JSONParser parser = new JSONParser();
        logger.info("LogEventReader Start");
        try (Reader is = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(is);
            String currentLine;
            while((currentLine=bufferedReader.readLine()) != null) {
                JSONObject logLine = (JSONObject) parser.parse(currentLine);
                FileLogEvent event = new FileLogEvent(logLine);
                logger.info("Event Read : "+event);
                try{
                    queue.put(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                Event e = new EOFEvent();
                queue.put(e);
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException | ParseException e) {
            logger.error( e.getMessage(), e);
            throw new IllegalArgumentException("Error file:"+e);
        }
        logger.info("LogEventReader End");
    }
}
