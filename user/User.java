import db.MalleDB;
import util.Options;

import java.util.Scanner;
import java.util.Arrays;

import java.net.HttpURLConnection;
import java.net.URL;

public class User {

    public static void main(String arg[]){

        Scanner scanner = new Scanner(System.in);   // System.in은 모든 Scanner가 공유하므로 하나의 Scanner를 쓰는 것이 좋다.

        while(true){
            System.out.println("==================Select Number===================");
            System.out.println("||1. Insert || 2. Read || 3. Quit||");
            System.out.println("==================================================");
            boolean brk = false;
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

    private static void wait_for_res(){
        // Wait for Response from Master
    }

    private static void send_to_master(String filename, String unique_key){
        // User Sends metadata to the Master
    }

    private static void insert(Scanner scanner){
        System.out.println("Put the Filename to insert");
        String filename = scanner.next();
        System.out.println("Put the Unique Key");
        String unique_key = scanner.next(); 
        
        send_to_master(filename, unique_key);
        wait_for_res();
    }

    private static void read(Scanner scanner){
        System.out.println("Getting Filename from User");
        String filename = scanner.next();
        System.out.println("Getting Unique Key from User");
        String unique_key = scanner.next();

        send_to_master(filename, unique_key);
        wait_for_res();
    }    



}