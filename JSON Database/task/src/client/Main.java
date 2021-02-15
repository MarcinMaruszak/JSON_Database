package client;

import com.beust.jcommander.JCommander;
import domain.Request;

public class Main {

    public static void main(String[] args) {
        Request<?,?> request = new Request<>();
        JCommander.newBuilder()
                .addObject(request)
                .build()
                .parse(args);
        new Client(request).start();
    }
}
