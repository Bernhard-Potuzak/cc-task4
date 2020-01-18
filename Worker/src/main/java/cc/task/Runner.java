package cc.task;


import org.apache.commons.net.util.SubnetUtils;
import org.glassfish.jersey.client.ClientProperties;

import javax.json.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;


public class Runner implements Runnable {
    private Client client;

    public boolean stopThread;

    private Runner() {
        stopThread = false;
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 2000);
    }

    static private Runner instance;

    static public Runner getInstance() {
        if (instance == null) {
            instance = new Runner();
        }
        return instance;
    }

    public void run() {
        try {
            Thread.sleep(5000);

        } catch (Exception ignored) { }
        System.out.println("CPanel Running. Use /config with ConfigJson to set Mapping. Use /startSystem after config to start ");
        while (!stopThread) {
            try {
                Thread.sleep(2000);
                System.out.println("Im Running");
            } catch (Exception e) {
                System.out.println("Thread could not sleep or some reason: " + e);
            }
        }

    }
}

    /*
    String getSectionJsonString(){
        JsonObjectBuilder main = Json.createObjectBuilder();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for (Section sec : sections){
            jab.add(Json.createObjectBuilder()
                    .add("id", sec.id)
                    .add("description", sec.desc)
            );
        }
        return main.add("sections", jab).build().toString();
    }

    String getCameraJsonString(){
        JsonObjectBuilder main = Json.createObjectBuilder();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for (Camera cam : cameras){
            jab.add(Json.createObjectBuilder()
                    .add("id", cam.id)
                    .add("type", cam.type)
                    .add("section", cam.getSectionId())
            );
        }

        return main.add("cameras", jab).build().toString();
    }
}*/
