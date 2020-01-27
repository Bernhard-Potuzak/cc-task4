package cc.task;


import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.PrintWriter;
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

    private String makeErrorJson(String msg, int statusCode){
        JsonObjectBuilder main = Json.createObjectBuilder();
        return main.add("Status", statusCode).add("MSG", msg).build().toString();
    }

    @GET
    @Path("/ping")
    public String getMsg()
    {
        return "Pinged jaaaaaa";
    }

    @POST
    @Path("/getStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus(){
        Runner runner = Runner.getInstance();
        JsonObjectBuilder bodyJson = Json.createObjectBuilder();
        bodyJson.add("file", runner.fileName);
        bodyJson.add("numVal", runner.getDbMap().getSize());
        return Response.status(200).entity(bodyJson.build().toString()).build();

    }

    @POST
    @Path("/insert")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response insert(@FormDataParam("k") String k, @FormDataParam("v") String jsonRaw){
        Runner runner = Runner.getInstance();
        String fileName = runner.getVolPath() + "/" + k + ".json";
        File file = new File(fileName);

        try {

            if (!file.createNewFile()) {
                
            }

        } catch (Exception e){
            System.out.println("Could not read/create file " + fileName);
            return Response.status(500).build();
        }






        /*

        try{
            runner.getDbMap().put(k, jsonRaw);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).build();
        */
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response delete(@FormDataParam("k") String k){
        Runner runner = Runner.getInstance();
        try{
            runner.getDbMap().remove(k);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).build();
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@FormDataParam("k") String k){
        Runner runner = Runner.getInstance();
        String ret = "";
        try{
            ret = runner.getDbMap().get(k);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).entity(ret).build();
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

