import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class DownloadUtility_Cache {
    private static final int BUFFER_SIZE = 4096;
 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    private static final int CACHE_SIZE = 2;
    public static Boolean fileExists(String filePath){
        File file = new File(filePath);
        if(file.exists() && !file.isDirectory()){
            return true;
        }
        return false;
    }

    public static void cacheOperations(){
        System.out.println("Checking Cache:");
        File cacheFolder = new File("./cache");
        File[] listOfFiles = cacheFolder.listFiles();

        Long lru=Long.MAX_VALUE;

        File toRemove = null;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
            if (listOfFiles[i].lastModified()<lru) {
                lru = listOfFiles[i].lastModified();
                toRemove = listOfFiles[i];
            }
            System.out.println("File " + listOfFiles[i].getName() + "Modified at : " + listOfFiles[i].lastModified());
            } else if (listOfFiles[i].isDirectory()) {
            System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        System.out.println("Total Files = "+listOfFiles.length);

        System.out.println("File to remove = "+toRemove.getName());

        if(listOfFiles.length >= CACHE_SIZE){
            System.out.println("Deleting the File: "+toRemove.getName());
            toRemove.delete();
        }

    }

    public static void downloadFile(String fileURL, String saveDir) throws IOException {
        
        URL url = new URL(fileURL);
        System.out.println("URL is " + url.toString());
        System.out.println("protocol is " + url.getProtocol());
        System.out.println("authority is " + url.getAuthority());
        System.out.println("file name is " + url.getFile());
        System.out.println("host is " + url.getHost());
        System.out.println("path is " + url.getPath());
        System.out.println("port is " + url.getPort());
        System.out.println("default port is " + url.getDefaultPort());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        
        // always check HTTP response code first
        if (httpConn instanceof HttpURLConnection) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            //System.out.println("Content-Disposition = " + disposition);
            //System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
            
            if(!fileExists(saveFilePath)){
                cacheOperations();
                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
     
                outputStream.close();
                inputStream.close();
     
                System.out.println("File downloaded");
                System.out.println("Saved file At :"+saveFilePath);
            }
            else
                System.out.println("File Exists already");
            //Now exec command to open the downloaded fiel using external viewer
            openfile(saveFilePath);
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    public static void openfile(String fileName){
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("xdg-open "+fileName);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println(s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}

    }
    public static void main(String[] args) {
        //String fileURL = "http://jdbc.postgresql.org/download/postgresql-9.2-1002.jdbc4.jar";
        //String fileURL = "http://www.tutorialspoint.com/java/java_tutorial.pdf";
        if(args.length>0){
            String fileURL=args[0];
            String saveDir = "./cache";
            

            try {
                DownloadUtility_Cache.downloadFile(fileURL, saveDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            System.out.println("Enter the URL");
        }
    }
}
