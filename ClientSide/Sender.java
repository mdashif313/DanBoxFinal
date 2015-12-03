import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author Ashif
 */


public class Sender extends Thread{

    private Socket sock;
    private Scanner in;
    private DataOutputStream output;
    private String sender;
    public String sourceDirectory = "";
    private ArrayList<String>fileList;
    public Sender(Socket sock, String sender,String source) throws IOException{
        
        this.sock=sock;
        this.sender=sender;
        this.sourceDirectory=source;
        in=new Scanner(System.in);
        output= new DataOutputStream(sock.getOutputStream());
        //this.fileList = fileList;, ArrayList<String>fileList
    }
    
    
    public void run() {               
        String sourcePath=sourceDirectory.substring(0,sourceDirectory.length()-1);
        while (true) { 
            try {
                Thread.sleep(5000);            
                ClientSynchronizer cs=new ClientSynchronizer(sourcePath);
                ArrayList<String> list=cs.Synchronizer();

                for(int i=0; i<list.size(); i++){                   
                    sendFile(list.get(i));
                    System.out.println(i);
                }                
                
            } catch (Exception e) {
                System.out.println("Exception inside client Sender.java "+e);
            }
        }
        
    }
    void sendFile(String fileName) {
        System.out.println(fileName);
        try {
            
            String FileToSend = sourceDirectory + fileName;
            
            //handle file read            
            File myFile = new File(FileToSend);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }
}
