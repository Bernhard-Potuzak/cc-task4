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
    @Path("insert")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response insert(@FormDataParam("k") String k, @FormDataParam("v") String jsonRaw){

        System.out.println(k);
        System.out.println(jsonRaw);

        Runner runner = Runner.getInstance();
        try{
            runner.getDbMap().put(k, jsonRaw);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).build();
    }

    @DELETE
    @Path("delete")
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

    @GET
    @Path("search")
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

