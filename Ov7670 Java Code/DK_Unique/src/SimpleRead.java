import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import java.util.Base64;
public class SimpleRead {
 private static final  char[]COMMAND = {'*', 'R', 'D', 'Y', '*'};
 private static final int WIDTH = 320; //640;
    private static final int HEIGHT = 240; //480;
   
    private static CommPortIdentifier portId;
    InputStream inputStream;
    SerialPort serialPort;

    public static void main(String[] args) {
      Enumeration portList = CommPortIdentifier.getPortIdentifiers();
     
        while (portList.hasMoreElements()) {
         portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
             System.out.println("Port name: " + portId.getName());
                if (portId.getName().equals("COM5")) {
                 SimpleRead reader = new SimpleRead();
                }
            }
        }
    }

    public SimpleRead() {
        int[][]rgb = new int[HEIGHT][WIDTH];
        int[][]rgb2 = new int[WIDTH][HEIGHT];
     
     try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 1000);
            inputStream = serialPort.getInputStream();

            serialPort.setSerialPortParams(1000000,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

         int counter = 0;

         while(true) {
          System.out.println("Looking for image");
         
          while(!isImageStart(inputStream, 0)){};
         
          System.out.println("Found image: " + counter);
          
          for (int y = 0; y < HEIGHT; y++) {
           for (int x = 0; x < WIDTH; x++) {
            int temp = read(inputStream);
         rgb[y][x] = ((temp&0xFF) << 16) | ((temp&0xFF) << 8) | (temp&0xFF);
           }
          }
          
          //System.out.println(java.util.Arrays.deepToString((rgb)));
          for (int y = 0; y < HEIGHT; y++) {
           for (int x = 0; x < WIDTH; x++) {
            rgb2[x][y]=rgb[y][x];
            
           }           
          }
           
          //System.out.println(rgb2);
          BMP bmp = new BMP();
         bmp.saveBMP("C:/Users/DK/Downloads/dk/" + (counter++) + ".bmp", rgb2);      
         System.out.println("Saved image: " + counter);
         
       
         }
  } catch (Exception e) {
	  	 
	  	  System.out.print("Hata");
  }
    }
    
    private void copyFileUsingApacheCommonsIO(File path, File path1) throws IOException {
 
     Files.copy(path.toPath(), path1.toPath());
 }

 private int read(InputStream inputStream) throws IOException {
     int temp = (char) inputStream.read();
  if (temp == -1) {
   throw new  IllegalStateException("Exit");
  }
  return temp;
    }
     
    private boolean isImageStart(InputStream inputStream, int index) throws IOException {
     if (index < COMMAND.length) {
      if (COMMAND[index] == read(inputStream)) {
       return isImageStart(inputStream, ++index);
      } else {
       return false;
      }
     }
     return true;
    }
}