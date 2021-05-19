
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileServer extends Thread {

    private Socket ss;
    private Socket s;

//    public FileServer(String host, int port, String file) {
//        try {
//            ss = new Socket(host, port);
////			sendFile(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) {

        try {
            Socket s = new Socket("localhost", 3210);
            System.out.println("Ket noi toi Master server - Localhost:3210");

            InputStream is = s.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            OutputStream os = s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            String sentMessage = "";
            String receivedMessage;
            List<String> fileNames = new ArrayList<String>();
            List<String> filePaths = new ArrayList<String>();
            String[] pathnames;
            File f = new File("files");
            pathnames = f.list();
            System.out.println("Danh sach cac file:");
            for (String pathname : pathnames) {
                Path path = Paths.get("files\\" + pathname);
                long bytes = Files.size(path);
                fileNames.add("files/" + pathname + " - " + bytes + "B");
                System.out.println(pathname);
                filePaths.add("files\\" + pathname);
            }
            do {
                for (String file : fileNames) {
                    bw.write(file);
                    bw.newLine();
                }

                bw.flush();

                if (sentMessage.equalsIgnoreCase("quit")) {
                    break;
                } else {
//                    br.lines().forEach(System.out::println);
                    String port = br.readLine().split(":")[1];

                    try {
                        System.out.println("Waiting for a Client");
                        ServerSocket serverFileServer = new ServerSocket(Integer.parseInt(port) + 30);
                        Socket socket = null;

                        while (true) {
                            try {
                                socket = serverFileServer.accept();
                                new FileServerHandler(socket, filePaths).start();
                            } catch (IOException e) {
                                System.out.println("I/O error: " + e);
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("There're some error" + e);
                        System.out.println("There're some error");
                    }

                }

            } while (true);

        } catch (IOException e) {
            System.out.println("There're some error");
        }

//        FileServer fc = new FileServer("localhost", 1988, "cat.jpg");
//        fc.start();
    }

}

class FileServerHandler extends Thread {

    final Socket ss;
    final List<String> filePaths;

    // Constructor
    public FileServerHandler(Socket s, List<String> filePaths) {
        this.ss = s;
        this.filePaths = filePaths;
    }

    public void sendFile(List<String> files) throws IOException {
        DataOutputStream dos = new DataOutputStream(ss.getOutputStream());
        byte[] done = new byte[3];
        byte[] buf = new byte[4096];
        String str = "done";  //randomly anything
        done = str.getBytes();
        int n = 0;
        for (int i = 0; i < files.size(); i++) {
            System.out.println("Sending: "+files.get(i));
            FileInputStream fis = new FileInputStream(files.get(i));
            while ((n = fis.read(buf)) != -1) {
                dos.write(buf, 0, n);
                System.out.println("Sent: "+ n+" B");
                dos.flush();
            }
            //should i close the dataoutputstream here and make a new one each time?                 
            dos.write(done, 0, 3);
            dos.flush();
        }
//or is this good?
        dos.close();
//        DataOutputStream dos = new DataOutputStream(ss.getOutputStream());
//        FileInputStream fis = new FileInputStream(file);
//        byte[] buffer = new byte[15380];
//
//        while (fis.read(buffer) > 0) {
//            dos.write(buffer);
//        }
//
//        fis.close();
//        dos.close();
    }

    public void run() {

        try {
            InputStream is = ss.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            do {
                OutputStream os = ss.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                String receivedMessage;

                do {
                    receivedMessage = br.readLine();
                    System.out.println("Received :" + ss.getPort() + ":" + receivedMessage);
                    if (receivedMessage.equalsIgnoreCase("quit")) {
                        System.out.println("Client has left !");
                        break;
                    } else {
                        List<String> fileNames = new ArrayList<String>();
                        String[] model = receivedMessage.split(",");
                        for (int i = 0; i < model.length; i++) {

                            fileNames.add(filePaths.get(Integer.parseInt(model[i]) - 1));
                        }
                        sendFile(fileNames);
//                        sendFile("files\\cat.jpg");
//                        //DataInputStream din=new DataInputStream(System.in);
//                        String k = "din.readLine();";
//                        bw.write(k);
//                        bw.newLine();
//                        bw.flush();
                    }
                } while (true);
                bw.close();
                br.close();
            } while (true);
        } catch (IOException e) {
            System.out.println("Done");
        }
    }
}
