import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Worker {

    boolean isInported;
    List<JSON_Formatter> allData;
    String url;
    private static Worker instance = null;
    private CloseableHttpClient httpClient;
    boolean hasCSV;
    private BufferedWriter staticWriter;
    private CSVPrinter staticPrinter;

    private Worker() {
        this.isInported = false;
        url = null;
        this.allData = new ArrayList<>();
        httpClient = HttpClients.createDefault();
        hasCSV = false;

    }

    public static Worker getWorker(){
        if (instance == null){
            instance = new Worker();
        }
        return instance;
    }

    int ranPlace(){
        Random r = new Random();
        return r.nextInt(allData.size()-1);
    }

    void setUrl(String url){
        this.url = url;
    }

    boolean hasUrl(){
        return url != null;
    }

    private CloseableHttpResponse execMultiPart(MultipartEntityBuilder meb, String url){
        CloseableHttpResponse res = null;
        HttpPost post = new HttpPost(url);
        post.setEntity(meb.build());
        try {
            res = httpClient.execute(post);
        } catch(Exception e){
            System.out.println("Could not execute " + e.toString() + " " + url);
            return null;
        }

        return res;

    }

    // Hashing Function with 64bit Initioal Hash + 64 Prime
    public long hashMe(String k){
        long hash = 0xcbf29ce484222325L;
        long prime =  0x100000001b3L;
        int kLen = k.length();
        for (int i = 0; i < kLen; i++){
            hash ^= k.charAt((i));
            hash *= prime;
        }

        return hash;
    }

    void printToCSV(long timestamp, String action, long timeUsed) {
        if (!hasCSV) {
            try {
                staticWriter = Files.newBufferedWriter(Paths.get("./" + "staticFile.csv"));
                staticPrinter = new CSVPrinter(staticWriter, CSVFormat.EXCEL.withHeader("timestamp", "action", "time_used"));
            } catch (IOException e) {
                System.out.println("Error opening");
                return;
            }
        }
        try{
            staticPrinter.printRecord(100, "Test", 50);
            staticPrinter.flush();
        } catch (Exception e){
            System.out.println("Could not write to static file: " + e.toString());
        }

    }

    long pingUrl(){
        long startTime = System.currentTimeMillis();
        HttpGet httpget = new HttpGet(url + "/ping");
        try {
            HttpResponse res = httpClient.execute(httpget);
            System.out.println("Responded: " + res.getStatusLine().getStatusCode());
        }catch(Exception e){
            System.out.println("Could not ping: " + e.toString());
        }
        return System.currentTimeMillis() - startTime;
    }

    void insertAll(){
        for(int i = 0; i < allData.size(); i++){
            insertToWorker(i);
        }
    }

    String testRun(String fileName){

        CSVPrinter csvPrinter = null;
        BufferedWriter writer = null;

        if (!fileName.contains(".csv")){
            return "File Name should contain the ending .csv";
        }
        try {
            writer = Files.newBufferedWriter(Paths.get("./" + fileName));
            csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader("timestamp", "action", "time_used"));
        }catch(IOException e){
            return "Error while opening File: " + e.toString();
        }

        long timeUsed;

        for (int i = 1; i <= 4; i++){
            setUrl("http://localhost:1010" + i + "/Worker");
            timeUsed = pingUrl();
            try {
                csvPrinter.printRecord(System.currentTimeMillis(), "Ping", timeUsed);
            } catch (Exception ignore){}

        }

        /*
        setUrl("http://localhost:50001");
        timeUsed = pingUrl();
        try{
            System.out.println("ping " + url + " used ms " + timeUsed);
            csvPrinter.printRecord(System.currentTimeMillis(), "Ping", timeUsed);
        } catch (Exception ignored){}
*/


        setUrl("http://localost:10101/Worker");

        for (int i = 0; i < 2; i++) {
            try {
                timeUsed = insertToWorker(ranPlace());
                csvPrinter.printRecord(System.currentTimeMillis(), "Insert", timeUsed);
            } catch (Exception e) {
                System.out.println("Cannot insert: " + e.toString());
            }
        }


        try {
            csvPrinter.flush();
            writer.close();
        } catch (Exception ignored){}
        return "Ended RestRun successfully";

    }

    long delete(String key){
        long start = System.currentTimeMillis();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k", key, ContentType.TEXT_PLAIN);

        CloseableHttpResponse res = execMultiPart(builder, url + "/delete");

        if (res != null) System.out.println("Delete responded with Code: " + res.getStatusLine().getStatusCode());

        String body = readResponse(res);
        long finish = System.currentTimeMillis();
        System.out.println("Delete Took " + (finish-start) + " ms to Execute");
        return finish-start;
    }

    long range(String keyLower, String keyUpper){
        long start = System.currentTimeMillis();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k1", keyLower, ContentType.TEXT_PLAIN);
        builder.addTextBody("k2", keyUpper, ContentType.TEXT_PLAIN);

        CloseableHttpResponse res = execMultiPart(builder, url + "/range");

        String body = readResponse(res);
        long finish = System.currentTimeMillis();
        System.out.println("Range Took " + (finish-start) + " ms to Execute");
        return finish-start;
    }



    long searchWorker(String key){
        long start = System.currentTimeMillis();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k", key, ContentType.TEXT_PLAIN);

        CloseableHttpResponse res = execMultiPart(builder, url + "/search");

        String body = readResponse(res);
        long finish = System.currentTimeMillis();
        System.out.println("Search Took " + (finish-start) + " ms to Execute");
        return finish-start;
    }

    private String readResponse(CloseableHttpResponse res) {
        String body = null;
        if (res != null){
            System.out.println("Insert responded with Code: " + res.getStatusLine().getStatusCode());
        } else {
            System.out.println("Respond was null");
            return "";
        }
        try {
            HttpEntity resEntity = res.getEntity();
            body = EntityUtils.toString(resEntity);
            EntityUtils.consume(resEntity);


        } catch (Exception e){
            System.out.println("Could not read Response Body");
        }

        if (body != null){
            System.out.println("Response: " + body);
        }

        return body;
    }


    long insertToWorker(int place){
        if (place >= allData.size() || place < 0){
            System.out.println("Place out of Bounds");
            return -1;
        }
        long start = System.currentTimeMillis();

        JSON_Formatter data = allData.get(place);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k", data.key, ContentType.TEXT_PLAIN);
        builder.addTextBody("v", data.value, ContentType.APPLICATION_JSON);

        CloseableHttpResponse res = execMultiPart(builder, url + "/insert");

        String body = readResponse(res);

        long finish = System.currentTimeMillis();
        System.out.println("Insert Took " + (finish-start) + " ms to Execute. (Inserted " + data.key + ")");
        return finish-start;

    }

    void importCSV() throws FileNotFoundException {
        String fileName = getClass().getClassLoader().getResource("netflix.csv").getFile();
        boolean gotHeader = false;
        List<String> headers = new ArrayList<>();
        if (fileName == null){
            throw new FileNotFoundException();
        }
        File csv = new File(fileName);
        Reader in = new FileReader(fileName);
        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.RFC4180.parse(in);
        }catch(Exception e){
            System.out.println("Could not parse csv: " + e.toString());
            return;
        }

        for (CSVRecord record : records){
            if (!gotHeader){
                for (int i = 0; i < record.size(); i++){
                    headers.add(record.get(i));
                }
                gotHeader = true;
            } else {
                JSON_Formatter temp = new JSON_Formatter(headers, record);
                allData.add(temp);
            }
        }
        this.isInported = true;
        System.out.println("Successfully Import");
    }
}
