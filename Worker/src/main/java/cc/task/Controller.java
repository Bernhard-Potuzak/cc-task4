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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    private String makeStatusJson(String fileCreatedName, boolean fileCreated, String fileName, long fileSize, String body){
        JsonObjectBuilder main = Json.createObjectBuilder();
        return main.add(fileCreatedName, fileCreated).add("fileName", fileName).add("fileSize", fileSize).add("workerId", Runner.getInstance().getId()).add("RespBody", body).build().toString();
    }

    @GET
    @Path("/ping")
    public Response getMsg()
    {
        return Response.status(200).build();
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

        if (k == null || jsonRaw == null){
            return Response.status(400).entity(makeErrorJson("Param k or v not set!", 400)).build();
        }

        if (k.isEmpty() || jsonRaw.isEmpty()){
            return Response.status(400).entity(makeErrorJson("Param k or v not set!", 400)).build();
        }

        Runner runner = Runner.getInstance();
        String fileName = runner.getVolPath() + "/" + k + ".json";
        File file = new File(fileName);
        boolean createdFile = true;

        try {
            if (!file.createNewFile()) {
                createdFile = false;
                PrintWriter writer = new PrintWriter(file);
                writer.close();
            }
        } catch (Exception e){
            System.out.println("Could not read/create file " + fileName);
            return Response.status(500).entity(makeErrorJson(e.toString(), 500)).build();
        }

        try{
            PrintWriter writer = new PrintWriter(file);
            writer.print(jsonRaw);
            writer.close();
        } catch (Exception e){
            System.out.println("Cannot print to file " + fileName);
            return Response.status(500).entity(makeErrorJson(e.toString(), 500)).build();
        }

        String resString = makeStatusJson("File_created", createdFile, fileName, file.length(),"");

        return Response.status(200).entity(resString).build();

        /*
        try{
            runner.getDbMap().put(k, jsonRaw);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).build();
        */
    }

    @POST
    @Path("/delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response delete(@FormDataParam("k") String k){

        if (k == null){
            return Response.status(400).entity(makeErrorJson("Param k not set", 400)).build();
        }

        if (k.isEmpty()){
            return Response.status(400).entity(makeErrorJson("Param k not set!", 400)).build();
        }

        Runner runner = Runner.getInstance();
        String fileName = runner.getVolPath() + "/" + k + ".json";
        File file = new File(fileName);
        boolean fileDeleted;

        try {
            fileDeleted = Files.deleteIfExists(file.toPath());
        } catch (Exception e){
            System.out.println("Could not delete file " + fileName);
            return Response.status(500).entity(makeErrorJson(e.toString(), 500)).build();
        }

        String resString = makeStatusJson("file_deleted", fileDeleted, fileName, 0,"");
        return Response.status(200).entity(resString).build();

        /*
        Runner runner = Runner.getInstance();
        try{
            runner.getDbMap().remove(k);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).build();

         */
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@FormDataParam("k") String k){
        if (k == null){
            return Response.status(400).entity(makeErrorJson("Param k not set", 400)).build();
        }

        if (k.isEmpty()){
            return Response.status(400).entity(makeErrorJson("Param k not set!", 400)).build();
        }

        Runner runner = Runner.getInstance();
        String fileName = runner.getVolPath() + "/" + k + ".json";
        File file = new File(fileName);
        boolean exist = file.exists();
        String resString = "";

        if (!exist){
            resString = makeStatusJson("file_exist", false, fileName, 0,"");
            return Response.status(404).entity(resString).build();
        }

        String content = "";
        try{
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception e){
            return Response.status(500).entity(makeErrorJson(e.toString(), 500)).build();
        }

        resString = makeStatusJson("file_exist", true, fileName, content.length(),content);
        return Response.status(200).entity(resString).build();


        /*
        Runner runner = Runner.getInstance();
        String ret = "";
        try{
            ret = runner.getDbMap().get(k);
        } catch (Exception e) {
            return Response.status(400).entity(makeErrorJson(e.toString(),400)).build();
        }
        return Response.status(200).entity(ret).build();
        */

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

