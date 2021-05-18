
import java.io.*;
import java.net.*;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TCPClient extends Thread {

    private Socket s;

    public static void main(String arg[]) {

        try {

            Socket s = new Socket("localhost", 3210);

            System.out.println("Dang ket noi den " + s.getPort());
            InputStream is = s.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            OutputStream os = s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            String sentMessage = "";
            String receivedMessage;
            do {
//                System.out.println("Talking to Serversss");
//                DataInputStream din = new DataInputStream(System.in);
                sentMessage = "Ket noi tu" + s.getPort();
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();

                if (sentMessage.equalsIgnoreCase("quit")) {
                    break;
                } else {

//                    List<String> result = br.lines().collect(Collectors.toList());
//                    for(int i=0;i<result.size();i++){
//                           System.out.println(result.get(i));
//                    }
                    String[] mess = br.readLine().split("/n");
                    for (int i = 0; i < mess.length; i++) {
                        System.out.println(mess[i]);
                    }
                    String port="";
                    try {
                        System.out.print("Nhap port FileServer:");
                        DataInputStream dinFileServerA = new DataInputStream(System.in);
                        port = dinFileServerA.readLine();
                        Socket sFileServer = new Socket("localhost", Integer.parseInt(port) + 30);
                        System.out.println("Da ket noi den FileServer:" + port);
                        InputStream isFileServer = sFileServer.getInputStream();
                        BufferedReader brFileServer = new BufferedReader(new InputStreamReader(isFileServer));
                        OutputStream osFileServer = sFileServer.getOutputStream();
                        BufferedWriter bwFileServer = new BufferedWriter(new OutputStreamWriter(osFileServer));
                        String sentMessageFileServer = "";
                        String receivedMessageFileServer;
                        System.out.println("Danh sach cac file hien co:");
                        for (int i = 0; i < mess.length; i++) {
                            if (mess[i].contains(port)) {
                                System.out.println(mess[i].replace(port, i + ""));
                            }
                        }
                        System.out.println("Co the tai 1 hoac nhieu file cung luc. Vd: 1,2,3,4,...");
                        do {
                            System.out.print("Nhap danh sach muon chon:");
                            DataInputStream dinFileServer = new DataInputStream(System.in);
                            sentMessage = dinFileServer.readLine();

                            String[] filessentMessage = sentMessage.split(",");
//                            for (int i = 0; i < filessentMessage.length; i++) {
//                                bwFileServer.write(filessentMessage[i]);
//                                bwFileServer.newLine();
//                            }
                            bwFileServer.write(sentMessage);
                            bwFileServer.newLine();
                            bwFileServer.flush();

                            if (sentMessage.equalsIgnoreCase("quit")) {
                                break;
                            } else {

                                List<String> messListFile = new ArrayList<String>();
                                for (int i = 0; i < mess.length; i++) {

                                    if (mess[i].contains(sFileServer.getPort() - 30 + "")) {
                                        messListFile.add(mess[i]);

                                    }
                                }
                                DataInputStream dis = new DataInputStream(sFileServer.getInputStream());
                                FileOutputStream fos = null;
                                String[] files = sentMessage.split(",");
                                List<String> listNameFiles = new ArrayList<String>();

                                for (int i = 0; i < files.length; i++) {

                                    String[] model = messListFile.get(Integer.parseInt(files[i]) - 1).split("-");
                                    String fileNameSave = messListFile.get(Integer.parseInt(files[i]) - 1).replace(model[model.length - 1], "").replace(" ", "").replace(":", "").replace("-", "");
                                    listNameFiles.add(fileNameSave);
                                    int size = Integer.parseInt(model[model.length - 1].replace(" ", "").replace("B", ""));
//                                    dis = new DataInputStream(sFileServer.getInputStream());
//                                    fos = new FileOutputStream(fileNameSave);
//                                    byte[] buffer = new byte[4096];
//                                    System.out.println(fileNameSave + " " + size + " bytes.");
//                                    int filesize = 2000000; // Send file size in separate msg
//                                    int read = 0;
//                                    int totalRead = 0;
//                                    int remaining = filesize;
//                                    while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//                                        totalRead += read;
//                                        remaining -= read;
//                                        System.out.println("read " + totalRead + " bytes.");
//                                        fos.write(buffer, 0, read);
//                                    }

                                }

                                for (int i = 0; i < listNameFiles.size(); i++) {
                                    int n = 0;
                                    byte[] buf = new byte[4096];
                                    System.out.println("Receiving file: " + listNameFiles.get(i));
                                    //create a new fileoutputstream for each new file
                                    fos = new FileOutputStream(listNameFiles.get(i));
                                    //read file
                                    while ((n = dis.read(buf)) != -1 && n != 3) {
                                        fos.write(buf, 0, n);
                                        fos.flush();
                                    }
                                    fos.close();
                                }
                                fos.close();
                                dis.close();
                                break;

                            }

                        } while (true);

                        bwFileServer.close();
                        brFileServer.close();
                    } catch (IOException e) {
                        System.out.println("Mat ket noi voi FileServer:"+port);
                    }
                }

            } while (true);

            bw.close();
            br.close();
        } catch (IOException e) {
            System.out.println("There're some error");
        }
    }

    public void run() {
        while (true) {
            try {

                saveFile(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Socket clientSock) throws IOException {

    }
}
