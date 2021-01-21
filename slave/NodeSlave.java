import db.MalleDB;
import util.Options;

import java.util.Scanner;
import java.util.Arrays;

import java.net.HttpURLConnection;
import java.net.URL;

public class NodeSlave {

    public static void main(String arg[]){

        MalleDB malleDB = new MalleDB();
        Options option = new Options(/* This Should Be Customized */);
        malleDB.init(option);
        malleDB.create();

        Scanner scanner = new Scanner(System.in);   // System.in은 모든 Scanner가 공유하므로 하나의 Scanner를 쓰는 것이 좋다.
        System.out.println("==================Waiting For Request===================");
        while(wait_for_req()){
            boolean brk = false;
            // It should be notified by Master if it is INSERT or READ.
            int slct = scanner.nextInt();
            // PROCESS #1
            switch (slct) {
                case 1:
                    insert(scanner);
                    break;
                case 2:
                    read(scanner);
                    break;
                case 3:
                    brk=true;
                    break;
                default:
                    System.out.println("Wrong Number Typed.");
                    break;
            }
            if(brk) break;
        }

    }

    private static boolean wait_for_req(){
        // wait for REQUEST from Master
        return true;
    }

    private static void send_to_master(String filename, String unique_key){
        // PROCESS #8
    }

    private static void insert(Scanner scanner, MalleDB malleDB){
        // Getting Filename and Unique Key from Master
        System.out.println("Put the Filename to insert");
        String filename = scanner.next();
        System.out.println("Put the Unique Key");
        String unique_key = scanner.next(); 
        
        download_file();
        

        send_to_master(filename, unique_key);
    }

    private static void read(Scanner scanner){
        System.out.println("Getting Filename from User");
        String filename = scanner.next();
        System.out.println("Getting Unique Key from User");
        String unique_key = scanner.next();

        send_to_master(filename, unique_key);
    }    

}