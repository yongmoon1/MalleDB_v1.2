import bloomfilter.AbstractBloomFilter;
import bloomfilter.BasicBloomFilter;
import bloomfilter.BloomFilter;
import db.MalleDB;
import util.Options;

import java.util.Scanner;
import java.util.Arrays;

import java.net.HttpURLConnection;
import java.net.URL;

public class NodeMaster {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);   // System.in은 모든 Scanner가 공유하므로 하나의 Scanner를 쓰는 것이 좋다.
        System.out.println("Type Number of Expected Elements to be Inserted in the Filter");
        int expected_elements = scanner.nextInt();
        System.out.println("Type Desired False Positive Probability");
        double desired_p = scanner.nextDouble();

        MalleDB malleDB = new MalleDB();
        //Options option = new Options(Options.DB_TYPE.LEVELDB);
        malleDB.init();
        malleDB.create();

        BloomFilter bloomFilter = new BasicBloomFilter(desired_p, expected_elements);

        System.out.println("==================Waiting For Request===================");
        while(wait_for_req()){
            boolean brk = false;
            int slct = scanner.nextInt();
            switch (slct) {
                case 1:
                    insert(bloomFilter, malleDB, scanner);
                    break;
                case 2:
                    read(bloomFilter, malleDB, scanner);
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

    scanner.close();

    }

    private static boolean wait_for_req(){
        // wait for REQUEST
        return true;
    }

    private static String calculate(){
        int total_num = 100; // temporary (node number -> 0~9)
        return Integer.toString((int)Math.random() * total_num);
    }

    private static void activate(String node_num){
        System.out.println("Activating Node " + node_num);
    }
    
    private static void notify_to_usr(String message){
        System.out.println("Notify to User that the file is not in DB");
    }

    private static void insert(BloomFilter bloomFilter, MalleDB malleDB, Scanner scanner){
        System.out.println("Getting Filename from User");
        String filename = scanner.next();
        System.out.println("Getting Unique Key from User");
        String unique_key = scanner.next(); // PROCESS #1

        String hash_input = filename + unique_key;
        bloomFilter.addData(hash_input);    // PROCESS #2

        String node_num = calculate(); // PROCESS #3

        malleDB.insert(filename, node_num); // PROCESS #4

        activate(node_num); // PROCESS #5
    }

    private static void read(BloomFilter bloomFilter, MalleDB malleDB, Scanner scanner){
        System.out.println("Getting Filename from User");
        String filename = scanner.next();
        System.out.println("Getting Unique Key from User");
        String unique_key = scanner.next(); // PROCESS #1
        
        String hash_input = filename + unique_key;
        if(bloomFilter.isPresent(hash_input)){  // PROCESS #2
            String search_node= ""; // = malleDB.read(filename);   // PROCESS #3 MalleDB는 read시 출력밖에 하지 않음... 수정필요
            malleDB.read(filename);
            activate(search_node);  // PROCESS #4
        }
        else{    // PROCESS #3-FAIL
            notify_to_usr("FAIL");
        }
    }
    // private static void testWithOptimumSizeBitSet() {
    //     Arrays.stream(randomWords).forEach(bloomFilter::addData);

    //     int present = 0;
    //     for (String w : wordsNotPresent) {
    //         if (bloomFilter.isPresent(w)) {
    //             present++;
    //         }
    //     }
    // }
}
