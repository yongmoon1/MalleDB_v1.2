package bloomfilter.test;

import bloomfilter.BasicBloomFilter;
import bloomfilter.BloomFilter;
import db.MalleDB;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class Master {

    Scanner scanner = new Scanner(System.in);

    System.out.println("Type Number of Expected Elements to be Inserted in the Filter");
    static int expected_elements = scanner.nextInt();
    System.out.println("Type Desired False Positive Probability");
    static double desired_p = scanner.nextDouble();


    public static void main(String[] args) {
        MalleDB malleDB = new MalleDB();
        BloomFilter bloomFilter = new BasicBloomFilter(desired_p, expected_elements);
        while(1){
            System.out.println("==================Select Number===================");
            System.out.println("||1. Insert || 2. Read || 3. Quit||");
            System.out.println("==================================================");
            boolean brk = false;
            switch (scanner.nextInt()) {
                case 1:
                    implement_insert();
                    break;
                case 2:
                    
                    break;
                case 3:
                    brk=true;
                    break;
                default:
                    System.out.println("Wrong Number Typed.")
                    break;
            }
            if(brk) break;
        }
    }

    private static int caculate(){
        int node_num = 10 // temporary (node number -> 0~9)
        return (int)(Math.random()) * node_num;
    }

    private static void implement_insert(){
        System.out.println("Put the Filename");
        String filename = scanner.next();
        System.out.println("Put the Unique Key");
        String unique_key = scanner.next();
        String hash_input = filename + unique_key;
        bloomFilter.addData(hash_input);
        int node_num = caculate();
        MalleDB malleDB = new MalleDB();
        malleDB.init();
        malleDB.create();
    }

    private static void testWithOptimumSizeBitSet() {
        Arrays.stream(randomWords).forEach(bloomFilter::addData);

        int present = 0;
        for (String w : wordsNotPresent) {
            if (bloomFilter.isPresent(w)) {
                present++;
            }
        }
    }

    scanner.close();
}
