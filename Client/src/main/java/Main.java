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
        int modFac = 16;
        boolean showMenu = true;
        Worker worker = Worker.getWorker();
        Scanner sc = new Scanner(System.in);
        worker.setUrl("http://localhost:10101/Worker");
        while(true){
            if (showMenu) {
                System.out.println("What you want: " + '\n' +
                        "c -> Import CSV, x -> exit, " + '\n' +
                        "u -> set URL (Enter then type url), " + '\n' +
                        "p -> Ping Url, i -> Insert (enter then type 0-DataSize), " + '\n' +
                        "s -> search (Enter then key of data), " + '\n' +
                        "t -> testRun (Enter and then Filename.csv), " + '\n' +
                        "h -> hash a Value. (Enter then String), " + '\n' +
                        "m -> Show menu again, " + '\n' +
                        "a -> Insert all if imported");
                showMenu = false;
            }
            userinput = sc.next().charAt(0);
            switch(userinput){
                case 'a':
                    if (worker.isInported){
                        worker.insertAll();
                    }
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
