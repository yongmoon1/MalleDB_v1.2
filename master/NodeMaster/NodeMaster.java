import bloomfilter.AbstractBloomFilter;
import bloomfilter.BasicBloomFilter;
import bloomfilter.BloomFilter;
import db.MalleDB;
import util.Options;

import java.util.Arrays;

import java.net.HttpURLConnection;
import java.net.URL;



public class NodeMaster {
    // Default Values
    int expected_elements = 1000000;
    double desired_p = 0.01;

    public static void main(String[] args) {

        MalleDB malleDB = new MalleDB();
        Options option = new Options(Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB);
        malleDB.init(option);
        malleDB.create();

        BloomFilter bloomFilter = new BasicBloomFilter(desired_p, expected_elements);

        System.out.println("==================Waiting For Request===================");
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
        }

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

    private static void insert(BloomFilter bloomFilter){

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
