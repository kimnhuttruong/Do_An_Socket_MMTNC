
import java.io.*;
import java.net.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TCPServer extends Thread {

    public static void writing(String fileNameFiles, List<String> listData) {
        try {
            //Whatever the file path is.
            listData = listData.stream().distinct().collect(Collectors.toList());

            File statText = new File(fileNameFiles);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            for (String data : listData) {
                if (data != "") {
                    w.write(data);
                    w.write(System.getProperty("line.separator"));
                }
            }
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file files.txt");
        }
    }

    public static void main(String arg[]) {
        try {
            String fileNamePorts = "ports.txt";
            List<String> listPort = new ArrayList<String>();
            writing(fileNamePorts, listPort);
            ServerSocket s = new ServerSocket(3210);
            Socket socket = null;
            String fileNameFiles = "files.txt";
            System.out.println("Khoi tao Master server - Localhost:3210");
            System.out.println("Waiting for Client");
            while (true) {
                try {
                    socket = s.accept();
                    listPort.add(socket.getPort() + "");
                    writing(fileNamePorts, listPort);
                    new ClientHandler(socket, fileNameFiles, fileNamePorts).start();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                }
            }

        } catch (IOException e) {
            System.out.println("I/O error: " + e);
            System.out.println("There're some error");
        }

    }
}

class ClientHandler extends Thread {

    final Socket ss;
    final String fileNameFiles;
    final String fileNamePorts;

    // Constructor
    public ClientHandler(Socket s, String fileNameFiles, String fileNamePorts) {
        this.ss = s;
        this.fileNameFiles = fileNameFiles;
        this.fileNamePorts = fileNamePorts;
    }

    public void writing(String fileNameFiles, List<String> listData) {
        List<String> ports = reading(fileNamePorts);

        try {
            //Whatever the file path is.
            listData = listData.stream().distinct().collect(Collectors.toList());

            File statText = new File(fileNameFiles);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            for (String data : listData) {

                for (String port : ports) {
                    if (data != "" && data.contains(port)) {
                        w.write(data);
                        w.write(System.getProperty("line.separator"));
                        break;
                    }
                }
            }
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file files.txt");
        }
    }

    public List<String> reading(String fileNameFiles) {

        List<String> message = new ArrayList<String>();
        try {

            for (String line : Files.readAllLines(Paths.get(fileNameFiles))) {
                message.add(line);
            }

        } catch (Exception e) {
            System.out.println("Read " + e);
        }

        return message;
    }

    public void run() {

        try {
            System.out.println("Ket noi voi port " + ss.getPort());
            InputStream is = ss.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            OutputStream os = ss.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            String receivedMessage;

            do {
                boolean isFileServer = false;
                receivedMessage = br.readLine();
                receivedMessage = (ss.getPort() + " : " + receivedMessage);
                System.out.println(receivedMessage);
                List<String> message = new ArrayList<String>();
                message = reading(fileNameFiles);
                if (receivedMessage != null && receivedMessage != "" && receivedMessage.contains("files/")) {
                    message.add(receivedMessage.replace("files/", ""));
                    isFileServer = true;
                }

                writing(fileNameFiles, message);

                if (receivedMessage.equalsIgnoreCase("quit")) {
                    System.out.println("Client has left !");
                    break;
                } else {
                    message = reading(fileNameFiles);
                    bw.write("Hello client port:" + ss.getPort());

                    if (!isFileServer) {
                        bw.write("/n");
                        for (String mes : message) {
                            bw.write(mes + "/n");
//                            bw.newLine();

                        }

                    }
                    bw.newLine();
                    bw.flush();

                }
            } while (true);
            bw.close();
            br.close();

        } catch (IOException e) {
            System.out.println("Mat ket noi voi " + ss.getPort());
            List<String> add = new ArrayList<String>();
            List<String> message = reading(fileNameFiles);

            for (String mess : message) {
                System.out.println(mess);
                if (!mess.contains(ss.getPort() + "")) {
                    add.add(mess);
                }
            }
            writing(fileNameFiles, add);

        }
    }
}
