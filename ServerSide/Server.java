/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Student
 */
import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    
    
    private ServerSocket server_socket;
    public ArrayList<ClientThread> Client_list;

    
    public Server(int port) throws IOException{
        
        server_socket= new ServerSocket(port);
        Client_list= new ArrayList<ClientThread>();
        System.out.println("Server Window Port: "+port);
        
        
        while(true){
            try {
                
                Socket sock= server_socket.accept();
                String User_Name=Initializer(sock);
                
                ClientThread t= new ClientThread(sock, User_Name, Client_list);
                Client_list.add(t);
                t.start();
                
            } catch (Exception e) {
                System.out.println("Error inside public Server(int port) "+e);
            }
            
        }
    }
    
    
    
    public final String Initializer(Socket client_socket) throws IOException{
        
        DataInputStream input= new DataInputStream(client_socket.getInputStream());
        String user=input.readUTF().trim();
        
        return user;
    }
    
    
            
    public static void main(String[] args) throws IOException {
        
        Server chat_server= new Server(3500);
    }
}
