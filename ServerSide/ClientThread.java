import java.net.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Ashif
 */



public class ClientThread extends Thread{
    
    public Socket client_socket;
    private DataInputStream input;
    private DataOutputStream output;
    protected String User_Name;
    public static ArrayList<ClientThread> Client_list;
    //private String sourceDirectory = "C:/Users/student/Desktop/project/server/";
    private String sourceDirectory = "D:/Temp/server/";
    
    public ClientThread(Socket client_Socket, String User_Name, ArrayList<ClientThread> Client_list) throws IOException{
        
        this.client_socket= client_Socket;
        this.Client_list= Client_list;
        
        this.User_Name= User_Name;
        
        input= new DataInputStream(client_socket.getInputStream());
        output= new DataOutputStream(client_socket.getOutputStream());
    }
    
    
    @Override
    public void run(){
        
        StartUp(User_Name, sourceDirectory);
        while (true) {            
            try {
                
                //String str=input.readUTF().trim();
                int bytesRead;

                DataInputStream clientData = new DataInputStream(client_socket.getInputStream());

                String fileName = clientData.readUTF();

                OutputStream output = new FileOutputStream((sourceDirectory+fileName));
                long size = clientData.readLong();
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }

                //output.close();
                //clientData.close();

                System.out.println("File "+fileName+" received from client.");
                Broadcast(fileName, User_Name);
                
            } catch (Exception e) {
            }
        }
        
    }  
    
    
    
    public boolean SendToClient(String msg){
        try {
            sendFile(msg);
            //output.writeUTF(msg);
            return true;
            
        } catch (Exception e) {
            System.out.println("Exception inside ClientThread SendToClient(String msg) "+e);
            return false;
        }
        
    }
    
    void sendFile(String fileName) {
        try {
            System.out.println(fileName);
            String FileToSend = sourceDirectory+fileName;
            
            //handle file read            
            File myFile = new File(FileToSend);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = client_socket.getOutputStream();

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

    
    
    
    public static void StartUp(String User_Name, String ServerDirectory){
        try {
            ClientThread ct;
            for(int i=0; i<Client_list.size(); i++){
            
            ct= Client_list.get(i);
            
            if(!ct.User_Name.equalsIgnoreCase(User_Name))
                continue;
            
            
            String path=ServerDirectory.substring(0,ServerDirectory.length()-1);
            File folder= new File(path);
            File[] listOfFiles = folder.listFiles();
            
            for (int j = 0; j < listOfFiles.length; j++) {
                ct.sendFile(listOfFiles[j].getName());
            }
           } 
        } catch (Exception e) {
        }
    }
    
    public synchronized static void Broadcast(String msg, String User_id){
        
        int i;
        
        for(i=0; i<Client_list.size(); i++){
            
            ClientThread ct= Client_list.get(i);
            
            if(ct.User_Name.equalsIgnoreCase(User_id))
                continue;
            
            if(!ct.SendToClient(msg))
                remover(ct.User_Name);
        }
    }  
    
    
    
    public static void remover(String User_id){
        int i;
        
        for(i=0; i<Client_list.size(); i++){
            
            ClientThread ct= Client_list.get(i);
            
            if(ct.User_Name==User_id){
                Client_list.remove(i);
                return;
            }    
        }
    }
}
