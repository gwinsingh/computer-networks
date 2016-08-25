import java.io.*;
import java.net.*;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class tryfinal {
    private static final int BUFFER_SIZE = 4096;
 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
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
            System.out.println("fileName = " + fileName);
            
            //create a socket connection
            Socket client = new Socket(url.getHost(), url.getDefaultPort());
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            

            // opens input stream from the HTTP connection
            InputStream inputStream = client.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
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
            
            //Now exec command to open the downloaded fiel using external viewer
            openfile(fileName);
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
            String saveDir = ".";
            

            try {
                HttpDownloadUtility.downloadFile(fileURL, saveDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            System.out.println("Enter the URL");
        }
    }
}