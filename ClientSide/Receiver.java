import java.io.*;
import java.net.*;
import java.util.*;


/**
 *
 * @author Ashif
 */



public class Receiver extends Thread{
    
    private Socket sock;
    private DataInputStream input;
    private String sourceDirectory = "";
    private String User = "";
    public Receiver(Socket sock, String user, String source) throws IOException{
        
        this.sock=sock;
        input=new DataInputStream(sock.getInputStream());
        User = user;
        this.sourceDirectory=source;
    }
    
    
    public void run(){
        
        while(true){
            try {
                //System.out.println(input.readUTF());
                receiveFile();
            } catch (Exception e) {
                System.err.println("Exception inside receiver "+e);
            }
        }
    }
    
    void receiveFile() {
        try {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(sock.getInputStream());
            
            String str=clientData.readUTF();
            LogEntry(sourceDirectory, str);
            
            String fileName = sourceDirectory + str;
            
            OutputStream output = new FileOutputStream((fileName));
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            //output.close();
            //clientData.close();

            System.out.println("File "+fileName+" received from client.");
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }

    
    public static void LogEntry(String sourcePath, String fileName){
        try {    
            String path = sourcePath+"log/log.txt";
            File f = new File(path); 
            BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));            
            Scanner input = new Scanner(f);

            while (input.hasNextLine()) {
                String line = input.nextLine();
                if(line.equalsIgnoreCase(fileName)){
                    return;
                }
            }
            
            bw.write(fileName);
            bw.newLine();
            bw.flush();
        }
        catch(IOException ex) {
            System.out.println("Exception inside LogEntry "+ex);
        }
    }      
}
