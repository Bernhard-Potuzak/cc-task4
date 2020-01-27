import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        try{

            loop();
        } catch(Exception e){
            System.out.println("loop exited with error: " + e);
            return;
        }

        System.out.println("Programm ended");
    }

    static void printToCSV(CSVPrinter staticPrinter, long timestamp, String action, long timeUsed, String response) {
        try{
            staticPrinter.printRecord(timestamp, action, timeUsed, response);
        } catch (Exception e){
            System.out.println("Could not write to static file: " + e.toString());
        }
    }

    public static void loop() throws Exception {
        char userInput;
        BufferedWriter staticWriter;
        CSVPrinter staticPrinter;

        staticWriter = Files.newBufferedWriter(Paths.get("./" + "CSV_TimeStamp_" + System.currentTimeMillis() + ".csv"));
        staticPrinter = new CSVPrinter(staticWriter, CSVFormat.EXCEL.withHeader("timestamp", "action", "time_used", "response"));

        String response;
        long start;
        int modFac = 16;
        boolean showMenu = true;
        Worker worker = Worker.getWorker();
        Scanner sc = new Scanner(System.in);
        worker.setUrl("http://localhost:10101/Worker");
        while(true){
            if (showMenu) {
                System.out.println("What you want: " + '\n' +
                        "c -> Import CSV, " + '\n' +
                        "x -> exit, " + '\n' +
                        "u -> set URL (Enter then type url), " + '\n' +
                        "p -> Ping Url, " + '\n' +
                        "i -> Insert (enter then type 0-DataSize), " + '\n' +
                        "s -> search (Enter then key of data), " + '\n' +
                        "r -> range (Enter then key1 Enter then key2), " + '\n' +
                        "d -> delete (Enter then key of data), " + '\n' +
                        "t -> testRun (Enter and then Filename.csv), " + '\n' +
                        "l -> Check Data in place. Enter then place, " + '\n' +
                        "h -> hash a Value. (Enter then String), " + '\n' +
                        "m -> Show menu again, " + '\n' +
                        "a -> Insert all if imported");
                showMenu = false;
            }
            userInput = sc.next().charAt(0);
            switch(userInput){
                case'l':
                    
                case 'a':
                    if (worker.isInported){
                        worker.insertAll();
                    }
                    break;
                case'r':
                    String key1 = sc.next();
                    String key2 = sc.next();
                    start = System.currentTimeMillis();
                    response = worker.range(key1, key2);
                    printToCSV(staticPrinter, System.currentTimeMillis(),"range", System.currentTimeMillis()-start, response);
                    //printToCSV(staticPrinter, System.currentTimeMillis(), "range", worker.range(key1, key2));
                    break;
                case'd':
                    String d = sc.next();
                    start = System.currentTimeMillis();
                    response = worker.delete(d);
                    printToCSV(staticPrinter, System.currentTimeMillis(),"delete", System.currentTimeMillis()-start, response);
                    //printToCSV(staticPrinter, System.currentTimeMillis(),"delete", worker.delete(d));
                    break;
                case 'c':
                    worker.importCSV();
                    break;
                case 'h':
                    String h = sc.next();
                    long hashed = worker.hashMe(h);
                    System.out.println(h + " hashes to " + hashed + " and maps with mod " + modFac + " " + hashed%modFac);
                    break;
                case 'm':
                    showMenu = true;
                    break;
                case 'i':
                    if (worker.isInported) {
                        int p = sc.nextInt();
                        start = System.currentTimeMillis();
                        response = worker.insertToWorker(p);
                        printToCSV(staticPrinter, System.currentTimeMillis(),"insert", System.currentTimeMillis()-start, response);
                    } else {
                        System.out.println("Import csv first");
                    }
                    break;
                case 't':
                    System.out.println(worker.testRun(sc.next()));
                    break;
                case 's':
                    String p = sc.next();
                    start = System.currentTimeMillis();
                    response = worker.searchWorker(p);
                    printToCSV(staticPrinter, System.currentTimeMillis(),"search", System.currentTimeMillis()-start, response);
                    break;
                case 'p':
                    worker.pingUrl();
                    break;
                case 'x':
                    staticPrinter.flush();
                    return;
                case 'u':
                    worker.setUrl(sc.next());
                    break;
                default:
                    System.out.println(userInput + " not an option");
                    if (worker.hasUrl()) System.out.println("Url: " + worker.url);
            }
        }
    }
}
