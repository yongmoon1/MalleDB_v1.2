import java.io.*;

public class filestream {
    public static void main(String[] args){

        try{
            FileInputStream fis = new FileInputStream("src/testfile.txt");
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] buffer = new byte[100];
            int size = bis.read(buffer);
            System.out.println(new String(buffer));




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
