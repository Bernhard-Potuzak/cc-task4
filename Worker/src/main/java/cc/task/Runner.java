package cc.task;


import org.apache.commons.net.util.SubnetUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.mapdb.*;

import javax.json.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.File;
import java.nio.file.Files;


public class Runner implements Runnable {
    private Client client;
    private String volPath;

    public boolean stopThread;
    private int id;
    private DB db = null;
    private HTreeMap<String, String> dbMap;

    private Runner(int id, String volPath) {
        this.id = id;
        this.volPath = volPath;
        stopThread = false;


        try {
            this.db = DBMaker.fileDB(volPath + "/file.db").make();
            System.out.println("db created, create dbMap");
            this.dbMap = db
                    .hashMap("dbMap")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer(Serializer.STRING)
                    .createOrOpen();
        } catch (Exception e){
            System.out.println("Could not make MapDb: " + e.toString());
            throw new IllegalArgumentException(e);
        }


        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 2000);

        System.out.println("Runnter created without error");
    }

    static private Runner instance;

    static Runner getInstance() {
        if (instance == null) {
            int id = -1;
            String volPath = "/shared";
            try {
                id = Integer.parseInt(System.getenv("id"));
            } catch (Exception e){
                throw new IllegalArgumentException("Id is not an integer");
            }
            instance = new Runner(id, volPath);
        }
        return instance;
    }

    void closeDb(){
        if (this.db != null) this.db.close();
    }

    public HTreeMap<String, String> getDbMap() {
        return dbMap;
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
                System.out.println(System.getenv("id"));
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
