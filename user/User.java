import master.NodeMaster.NodeMaster

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.Scanner;
import java.util.Arrays;

import java.net.HttpURLConnection
import java.net.URL


public class User {

    private static final String USER_AGENT = "";
    private static final String GET_URL = "";
    private static final String POST_URL = "";

    public static void main(String arg[]){

        Scanner scanner = new Scanner(System.in);
        // PROCESS #1
        switch (arg[0]) {
            case 1:
                System.out.println("Put the Filename to insert");
                String filename = scanner.next();
                System.out.println("Put the Unique Key");
                String unique_key = scanner.next(); 
                insert(filename, unique_key);
                break;
            case 2:
                System.out.println("PUT the Filename to read");
                String filename = scanner.next();
                System.out.println("Put the Unique Key");
                String unique_key = scanner.next();
                read(filename, unique_key);
                break;
            default:
                System.out.println("Wrong Number Typed. Type 1 or 2. 1: Insert 2: Read");
                break;
        }

    }

    private static void wait_for_res(){
        // Wait for Response from Master
    }

    private static void insert(String filename, String unique_key){
        post_insert(filename, unique_key);
        wait_for_res();
    }

    private static void read(String filename, String unique_key){
        send_to_master(filename, unique_key);
        wait_for_res();
    }    

    private static void post_insert(String filename, String unique_key){
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        
        con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
        os.close();
        
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("GET request not worked");
		}

    }

}