package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.Request;
import domain.Response;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private JSONObject dataBase;
    private final String address;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService executors;
    FilesHandler filesHandler;

    public Server() {
        address = "127.0.0.1";
        port = 6667;
        executors = Executors.newFixedThreadPool(4);
        filesHandler = new FilesHandler();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
            System.out.println("Server started!");
            while (true) {
                executors.submit(new Session(serverSocket.accept()));
            }
        } catch (IOException ignored) {
            System.out.println("Server closed by client!");
        }
    }

    class Session extends Thread {
        private final Socket socket;

        public Session(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                ObjectMapper objectMapper = new ObjectMapper();
                Request<?,?> request1 = objectMapper.readValue( inputStream.readUTF(), Request.class);
                Response<?> response = getResponse(request1);
                outputStream.writeUTF(objectMapper.writeValueAsString(response));
                if (request1.getType().equals("exit")) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Response<?> getResponse(Request<?,?> request) {
            dataBase = filesHandler.getDataBase();
            switch (request.getType()) {
                case "get":
                    return getValue(request);
                case "set.json":
                case "set":
                    setValue(request);
                    filesHandler.saveDataBase(dataBase);
                    return new Response.ResponseBuilder<String>().response("OK").build();
                case "delete":
                    delete(request);
                    filesHandler.saveDataBase(dataBase);
                    return new Response.ResponseBuilder<String>().response("OK").build();
                case "exit":
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new Response.ResponseBuilder<String>().response("OK").build();
                default:
                    return new Response.ResponseBuilder<String>().response("ERROR").build();
            }
        }

        private Response<?> getValue(Request<?,?> request) {
            Object object = getJSONObjectOrValue(request);
            try {
                JSONObject jsonObject = (JSONObject) object;
                return new Response.ResponseBuilder<JSONObject>().response("OK").value(jsonObject).build();
            } catch (Exception e) {
                return new Response.ResponseBuilder<String>().response("OK").value(object.toString()).build();
            }
        }

        private Object getJSONObjectOrValue(Request<?,?> request) {
            String[] paths = getArray(request.getKey());
            String s = "";
            JSONObject jsonObject;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node;
            try {
                node = objectMapper.readTree(dataBase.toString());
                int i = 0;
                while (i < paths.length) {
                    s = node.path(paths[i].trim()).asText();
                    node = node.path(paths[i].trim());
                    i++;
                }
                if (node.isObject()) {
                    jsonObject = objectMapper.treeToValue(node, JSONObject.class);
                    return jsonObject;
                } else {
                    return s;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void setValue(Request<?,?> request) {
            String[] paths = getArray(request.getKey());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root= objectMapper.readTree(dataBase.toString());
                JsonNode node = getNode(request, root);
                if(node.isEmpty()){
                    dataBase.put(request.getKey(), request.getValue());
                }else {
                    ((ObjectNode) node).put(paths[paths.length-1].trim(), String.valueOf(request.getValue()));
                    dataBase = objectMapper.treeToValue(root, JSONObject.class);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        private void delete(Request<?,?> request) {
            String[] paths = getArray(request.getKey());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root= objectMapper.readTree(dataBase.toString());
                JsonNode node= getNode(request, root);
                ((ObjectNode) node).remove(paths[paths.length-1].trim());
                dataBase = objectMapper.treeToValue(root, JSONObject.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        private JsonNode getNode(Request<?,?> request, JsonNode root){
            String[] paths = getArray(request.getKey());
            int i = 1;
            JsonNode node = root.path(paths[0].trim());
            while (i < paths.length-1) {
                node = node.path(paths[i].trim());
                i++;
            }
            return node;
        }

        private String[] getArray(Object object){
            if(object.getClass()==String.class){
                return  ((String) object).split(",");
            }else {
                List<String> list = (List<String>) object;
                return list.toArray(new String[0]);
            }

        }
    }
}


