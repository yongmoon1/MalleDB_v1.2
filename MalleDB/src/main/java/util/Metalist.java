
package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.String;
import java.util.List;
// for many sallfile

public class Metalist {//목적 : 컨버트 기능과 Value를 String타입으로 단계적으로 추가를 가능하게 하기위해서

    private String key = new String();
    private String allvalue = new String();//data of smalfiles
    //devided data of smallfile
    private List<String> values = new ArrayList();

    public Metalist() {
    }

    // set key to metaid
    public void setkey(String key) {
        this.key = key;
    }

    public void setAllvalue(String value) {
        this.allvalue = value;
    }

    //make one String allvalue using list of smallfile's value
    public void makemerge() {
        for (String temp : values) {
            if (temp.endsWith("&")) {
                allvalue = allvalue.concat(temp);
            } else {
                allvalue = allvalue.concat(temp + "&");
            }
        }
    }

    public int makespilt() {
        String[] value;
        value = allvalue.split("&");

        //stored part values
        for (String temp : value) {
            values.add(temp);
        }


        return getsize();
    }

    //add small file's value at allvalue
    public void addlist(String value) {
        values.add(value);

    }

    public void addlistall(String[] value) {
        for (String temp : value) {
            values.add(temp);
        }
    }

    // return number of smallfile: purpose is counter
    public int getsize() {
        return values.size();
    }

    public String[] getvalues() {
        String[] value = new String[getsize()];
        for (int i = 0; i < getsize(); i++) {
            value[i] = values.get(i);
        }
        return value;
    }

    // purpose is check buffer size with data
    public int getvaluesize() {
        return allvalue.length();
    }

    public String getkey() {
        return key;
    }

    public String getallvalue() {
        return allvalue;
    }


    //in allvalue, export data using counter

}