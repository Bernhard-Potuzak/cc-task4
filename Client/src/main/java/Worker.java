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

import javax.json.Json;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Worker {

    boolean isInported;
    List<JSON_Formatter> allData;
    String url;
    private static Worker instance = null;
    private CloseableHttpClient httpClient;

    private Worker() {
        this.isInported = false;
        url = null;
        this.allData = new ArrayList<>();
        httpClient = HttpClients.createDefault();
    }

    public static Worker getWorker(){
        if (instance == null){
            instance = new Worker();
        }
        return instance;
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

    String testRun(String fileName){

        if (!fileName.contains(".csv")){
            return "File Name should contain the ending .csv";
        }
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("./" + fileName));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader("timestamp", "action", "time_used"));


            csvPrinter.printRecord(100, "Test", 50);

            csvPrinter.flush();

        }catch(IOException e){
            return "Error: " + e.toString();
        }

        return "Ended RestRun successfully";

    }

    void searchWorker(String key){
        long start = System.currentTimeMillis();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k", key, ContentType.TEXT_PLAIN);

        CloseableHttpResponse res = execMultiPart(builder, url + "/search");

        if (res != null) System.out.println("Insert responded with Code: " + res.getStatusLine().getStatusCode());

        InputStream body = null;
        try {
            body = res.getEntity().getContent();
        } catch (Exception e){
            System.out.println("Could not read Response Body");
        }

        if (body != null){
            System.out.println("Response: " + body.toString());
        }

        long finish = System.currentTimeMillis();
        System.out.println("Search Took " + (finish-start) + " ms to Execute");
    }

    void insertToWorker(int place){
        if (place >= allData.size() || place < 0){
            System.out.println("Place out of Bounds");
            return;
        }
        long start = System.currentTimeMillis();

        JSON_Formatter data = allData.get(place);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("k", data.key, ContentType.TEXT_PLAIN);
        builder.addTextBody("v", data.value, ContentType.APPLICATION_JSON);

        CloseableHttpResponse res = execMultiPart(builder, url + "/insert");

        if (res != null) System.out.println("Insert responded with Code: " + res.getStatusLine().getStatusCode());

        long finish = System.currentTimeMillis();
        System.out.println("Insert Took " + (finish-start) + " ms to Execute. (Inserted " + data.key + ")");

    }

    void pingUrl(){
        HttpGet httpget = new HttpGet(url + "/ping");
        try {
            HttpResponse res = httpClient.execute(httpget);
            System.out.println("Responded: " + res.getStatusLine().getStatusCode());
        }catch(Exception e){
            System.out.println("Could not ping: " + e.toString());
        }
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
