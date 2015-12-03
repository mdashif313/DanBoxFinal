import java.io.*;
import java.net.*;
import java.util.*;


/**
 *
 * @author Ashif
 */


public class Client {
    
    public static void initialize_client(Socket sock, String User_Name){
        try {
            
            DataOutputStream output= new DataOutputStream(sock.getOutputStream());
            output.writeUTF(User_Name);
            
        } catch (Exception e) {
            System.out.println("Exception Inside Client initialize_client "+e);
        }
      
    }
    
    public static void main(String[] args) throws IOException {
    
        Scanner in= new Scanner(System.in);
        System.out.println("Please give your username: ");
        String inFromUser=in.nextLine();
        System.out.println("Please give your directory: ");
        String source=in.nextLine();
        Socket sock=new Socket("localhost",3500);
        
        initialize_client(sock,inFromUser);
        LogCreator(source);
        /*System.out.println("Client Window");
        ArrayList<String>fileList = new ArrayList<String>(100);
        fileList.add("p.txt");
        fileList.add("s.txt");
        fileList.add("t.txt");*/
        
        new Thread(new Receiver(sock, inFromUser,source)).start();
        new Thread(new Sender(sock, inFromUser, source)).start();
        
        //new Thread(new Sender(sock, inFromUser, source, fileList)).start();
        //new Thread(new Receiver(sock, inFromUser,source)).start();
    }   

    
    /*
     * Client's log file initializer  
     */       
    public static void LogCreator(String sourcePath){
        try {
            String path = sourcePath+"log/log.txt";
            File f = new File(path);
            f.getParentFile().mkdirs(); 
            f.createNewFile();
            
            PrintWriter pw = new PrintWriter(f);
            pw.close();
            
        } catch (Exception e) {
            System.out.println("Exception inside LogCreator "+e);
        }
    }    
}
