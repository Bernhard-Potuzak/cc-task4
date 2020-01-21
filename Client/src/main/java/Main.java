import java.io.File;
import java.io.FileNotFoundException;
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


    public static void loop() throws Exception {
        char userinput;
        Worker worker = Worker.getWorker();
        Scanner sc = new Scanner(System.in);
        worker.setUrl("http://localhost:8080/Worker");
        while(true){
            System.out.println("What you want: ( c -> Import CSV, x -> exit, u -> set URL (Enter then type url), p -> Ping Url, i -> Insert (enter then type 0-DataSize), s -> search (Enter then key of data), t -> testRun (Enter and then Filename.csv");
            userinput = sc.next().charAt(0);
            switch(userinput){
                case 'c':
                    worker.importCSV();
                    break;
                case 'i':
                    if (worker.isInported) {
                        int p = sc.nextInt();
                        worker.insertToWorker(p);
                    } else {
                        System.out.println("Import csv first");
                    }
                    break;
                case 't':
                    System.out.println(worker.testRun(sc.next()));
                    break;
                case 's':
                    String p = sc.next();
                    worker.searchWorker(p);
                    break;
                case 'p':
                    worker.pingUrl();
                    break;
                case 'x':
                    return;
                case 'u':
                    worker.setUrl(sc.next());
                    break;
                default:
                    System.out.println(userinput + " not an option");
                    if (worker.hasUrl()) System.out.println("Url: " + worker.url);
            }
        }
    }
}
