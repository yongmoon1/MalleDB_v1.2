import java.io.*;

public class filestream {
    public static void main(String[] args){

        try{
            FileInputStream fis = new FileInputStream("src/testfile.txt");
            FileOutputStream fos = new FileOutputStream("output.txt");

            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 100);
            byte[] buffer = new byte[100];
            int size = bis.read(buffer);
            System.out.println(size);
            bos.write(buffer, 0, size);
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
