package master;

import bloomfilter.AbstractBloomFilter;
import bloomfilter.BasicBloomFilter;
import bloomfilter.BloomFilter;
import db.MalleDB;
import util.Options;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class Master {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type Number of Expected Elements to be Inserted in the Filter");
        int expected_elements = scanner.nextInt();
        System.out.println("Type Desired False Positive Probability");
        double desired_p = scanner.nextDouble();

        MalleDB malleDB = new MalleDB();
        Options option = new Options(Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB);
        malleDB.init(option);
        malleDB.create();

        BloomFilter bloomFilter = new BasicBloomFilter(desired_p, expected_elements);

        while(true){
            System.out.println("==================Select Number===================");
            System.out.println("||1. Insert || 2. Read || 3. Quit||");
            System.out.println("==================================================");
            boolean brk = false;
            switch (scanner.nextInt()) {
                case 1:
                    implement_insert(bloomFilter, malleDB);
                    break;
                case 2:
                    
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

    private static String calculate(){
        int total_num = 10; // temporary (node number -> 0~9)
        return Integer.toString((int)Math.random() * total_num);
    }

    private static void activate(String node_num){
        // 
    }

    private static void implement_insert(BloomFilter bloomFilter, MalleDB malleDB){
        Scanner scanner_ins = new Scanner(System.in);

        System.out.println("Put the Filename");
        String filename = scanner_ins.next();
        System.out.println("Put the Unique Key");
        String unique_key = scanner_ins.next(); // PROGRESS #1

        String hash_input = filename + unique_key;
        bloomFilter.addData(hash_input);    // PROGRESS #2

        String node_num = calculate(); // PROGRESS #3

        malleDB.insert(filename, node_num); // PROGRESS #4

        activate(node_num); // PROGRESS #5
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
