package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Request;
import server.FilesHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Request<?,?> request;

    public Client(Request<?,?> request) {
        this.request = request;
    }

    public void start() {
        String serverAddress = "127.0.0.1";
        int port = 6667;
        try (Socket socket = new Socket(InetAddress.getByName(serverAddress), port);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Client Started");

            ObjectMapper objectMapper = new ObjectMapper();
            if(request.getType()==null){
                String file = request.getFileName();
                request=new Request<>();
                request=new FilesHandler().getRequestFromFile(file);
            }else if(!request.getType().equals("exit")){
                Request<String, String> request1 = new Request<>();
                ArrayList<String> array = (ArrayList<String>) request.getKey();
                request1.setKey(array.get(0));
                request1.setType(request.getType());
                if(request.getType().equals("set.json")||request.getType().equals("set")){
                    ArrayList<String> array1 = (ArrayList<String>) request.getValue();
                    request1.setValue(array1.get(0));
                }
                request=request1;
            }

            outputStream.writeUTF(objectMapper.writeValueAsString(request));
            System.out.println("Sent: "+objectMapper.writeValueAsString(request));
            System.out.println("Received: "+inputStream.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
