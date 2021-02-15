package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FilesHandler {
    private ReadWriteLock lock;

    public FilesHandler() {
        lock = new ReentrantReadWriteLock();
    }

    public JSONObject getDataBase(){
        try (Reader reader = Files.newBufferedReader(
                Paths.get(System.getProperty("user.dir")+"/JSON Database/task/src/server/data/db.json"))){
            Lock readLock = lock.readLock();
            readLock.lock();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);
            JSONObject db = (JSONObject) obj;
            readLock.unlock();
            return db;
        } catch (IOException | ParseException e) {
            return new JSONObject();
        }
    }

    public void saveDataBase(JSONObject db){
        try (Writer writer = Files.newBufferedWriter(
                Paths.get(System.getProperty("user.dir")+"/JSON Database/task/src/server/data/db.json"))){
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            writer.write(db.toJSONString());
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request<?,?> getRequestFromFile(String filename){
        Request<?,?> request= new Request();
        ObjectMapper objectMapper = new ObjectMapper();
        try (Reader reader = Files.newBufferedReader(
                Paths.get(System.getProperty("user.dir")+"/JSON Database/task/src/client/data/"+filename))){
            request = objectMapper.readValue(reader, Request.class);
            return request;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("fail");
        }
        return request;
    }
}
