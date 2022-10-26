import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;


public class DataPrinter {

    public static class Task implements Runnable {

        private int name;
        private File directory;

        public Task(int name, File directory) {
            this.name = name;
            this.directory = directory;
        }

        public void run() {

            try {

                Path mainPath = Paths.get(System.getProperty("user.dir"));
                Path outPath1 = mainPath.resolve("out");
                Path outPath2 = outPath1.resolve(this.directory.getName());

                Files.createDirectories(outPath2);

                String pathZippato = "";

                for (File f : this.directory.listFiles()) {

                    pathZippato = outPath2 + "\\" + f.getName() + ".gz";

                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));

                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathZippato));
                    GZIPOutputStream zip = new GZIPOutputStream(out);

                    byte[] buffer = new byte[1024];
                    int len;

                    while((len = in.read(buffer)) != -1){
                        zip.write(buffer, 0, len);
                    }

                    zip.close();
                    out.close();
                    in.close();

                }

                System.out.printf("%s ha terminato il Task %s\n", Thread.currentThread().getName(),name);



            } catch (IOException e) {
                 System.err.println("Errore");
            }

        }

    }

    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        ExecutorService service = Executors.newFixedThreadPool(10);


        int n, i = 0;
        String arr[];

        System.out.println("Quante directory vuoi comprimere?");
        n = sc.nextInt();

        arr = new String[n];

        sc.nextLine();

        do {
            System.out.println("Inserisci la directory " + i);
            arr[i] = sc.nextLine();
            i++;
        } while (i < n);

        Path mainPath = Paths.get(System.getProperty("user.dir"));
        Path outPath = mainPath.resolve("out");

        try {
            Files.createDirectories(outPath);
        } catch (IOException e) {
            System.err.println("Errore");
        }

        for (int j  = 0; j < n; j++) {

            File f = new File(arr[j]);

            if (!f.isDirectory()) {
                System.out.println("Il percorso NON corrisponde ad una directory!");
                continue;
            }

            service.execute(new Task(j, f));

        }

        service.shutdown();

        if (!service.awaitTermination(60000, TimeUnit.SECONDS)) {
            System.err.println("I thread non sono stati completati entro i tempi!");
        }

        System.exit(0);

    }

}
    
