package cc.task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// docker run --rm --name collector1 --network mainNet -p 8080:8080 task1/collector

public class Manager implements ServletContextListener {

    private Thread mainThread;
    private Runner runner = null;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            if (runner == null) {
                runner = Runner.getInstance();
                mainThread = new Thread(runner);
                mainThread.start();
            }
        } catch (Exception e){
            System.out.println("Fail to initialize: " + e.toString());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if (runner != null) {
            runner.stopThread = true;
            try {
                mainThread.join();
            } catch(Exception e) {
                System.out.println("Thread couldnt join for some reason: " + e);
            }
        }
        if (runner != null) runner.closeDb();
        System.out.println("Bye Bye");

    }





}
