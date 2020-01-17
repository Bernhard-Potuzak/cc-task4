package cc.task;


import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

@Path("")
public class Controller {

    private JsonObject readJson(String jsonRaw){
        JsonObject bodyJson = null;
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonRaw));
            bodyJson = jsonReader.readObject();
            jsonReader.close();
        } catch (Exception e){
            System.out.println("Could not process JSON: " + e);
        }
        return bodyJson;
    }
    private String makeErrorJson(Exception e){


        return "jas";
    }

    @GET
    @Path("/ping")
    public String getMsg()
    {
        return "Pinged jaaaaaa";
    }



    /*
    @POST
    @Path("/pingCamera")
    public Response scanCameras(@QueryParam("testIp") String testIp){
        String result = "No Result";
        try {
            result = Runner.getInstance().scanCamera(testIp);
        }catch(Exception e){
            return Response.status(500).entity(result).build();
        }
        return Response.status(200).entity("Status successfully " + result).build();
    }*/

}

