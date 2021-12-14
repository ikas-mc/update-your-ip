package ikas.java.project.updateip;

import java.util.Scanner;

public class Launcher {
    private static volatile boolean isStopped = false;

    private static final App app = new App();

    public static void main(String[] args) {
        app.start();

        var sc = new Scanner(System.in);
        System.out.print("Enter 'stop' to exit: ");
        while (sc.nextLine().equalsIgnoreCase("stop")) {
            app.stop();
        }
    }


    public static void windowsService(String[] args) {
        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }

        if ("start".equals(cmd)) {
            windowsStart();
        } else {
            windowsStop();
        }
    }

    public static void windowsStart() {
        app.start();
        while (!isStopped) {
            synchronized (app) {
                try {
                    app.wait(60000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public static void windowsStop() {
        isStopped = true;
        synchronized (app) {
            app.notifyAll();
        }
    }

}
