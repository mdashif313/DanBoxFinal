

import java.util.*;
import java.io.*;
import java.net.*;
import static java.lang.System.currentTimeMillis;
import java.util.*;
import java.nio.file.*;
import java.nio.file.attribute.*;


/**
 *
 * @author Ashif
 */
public class ClientSynchronizer extends Thread {
    
    public static ArrayList<String> list;
    public static String clientDirectory;
    public static File folder;
    
    public ClientSynchronizer(String path) throws IOException{
        
        list = new ArrayList<String>();
        folder= new File(path);
        clientDirectory= path+"/";                

    }
    
    
    
    /*
     * A method to handle automatic synchronization
     * this method return list of newly added or
     * updated files to client directory to upload
     */
    public static ArrayList<String> Synchronizer(){
        
        try {                        
            ArrayList<String> logList= LogReader(clientDirectory);
            File[] listOfFiles = folder.listFiles();
            long previous_time= currentTimeMillis()- 5000;
            
            
             for (int i = 0; i < listOfFiles.length; i++) {
                
                String temp= clientDirectory+listOfFiles[i].getName();
                Path p = Paths.get(temp);
                BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
                long t=attr.creationTime().toMillis();
                
                if(listOfFiles[i].isFile() &&((t>previous_time)||(listOfFiles[i].lastModified()>previous_time))) {
                    
                    boolean key=true;
                    String currentFile=listOfFiles[i].getName();
                    
                    for(int j=0; j<logList.size(); j++){
                        if(logList.get(j).equalsIgnoreCase(currentFile)){
                            logList.remove(j);
                            key=false;
                        }     
                    }
                    
                    if(key){
                       list.add(currentFile); 
                    } 
                    
                    LogWriter(clientDirectory, logList);
                }
             }
            
        } catch (Exception e) {
            System.out.println("Excption inside ClientSYnchronizer "+e);
        }
              
        return list;
    }
    
    
    
    /*
     * A method to check previous log file
     */
    public static ArrayList<String> LogReader(String sourcePath){
        ArrayList<String> logList= new ArrayList<String>();
        try {
            String path = sourcePath+"log/log.txt";
            File f = new File(path);            
            Scanner input = new Scanner(f);

            while (input.hasNextLine()) {
                String line = input.nextLine();
                logList.add(line);
            }
        } catch (Exception e) {
        } 
        return logList;
    }



    /*
     * A method to update log file
     * after modification.
     */    
    public static void LogWriter(String sourcePath, ArrayList<String> fileList){
        try {    
            String path = sourcePath+"log/log.txt";
            File f = new File(path); 
            PrintWriter pw = new PrintWriter(f);
            pw.close();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
            
            for(int i=0; i<fileList.size(); i++){
                bw.write(fileList.get(i));
                bw.newLine();
                bw.flush();
            }
        }
        catch(IOException e) {
            System.out.println("Exception inside LogWriter "+e);
        }
    }  
        
}
