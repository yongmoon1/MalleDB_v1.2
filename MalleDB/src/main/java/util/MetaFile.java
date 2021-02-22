package util;

import java.util.Arrays;
import java.lang.String;

/**
 * Item.
 * <p>
 * See {@code cassandra2/README.md} for details.
 *
 * @author Tillo
 */


public class MetaFile {
    private int order;
    //1: medium block
    //2: blob
    //3: tiny

    private int[] counters;
    private int id;
    private int size;
    private String name;
    private String key;
    private String userid;
    private String Refrenceid;
    private boolean hot;
    private boolean isBackup;
    private boolean isinMemory;

    public MetaFile() {
        this.key = "eeeeeee";
    }

    public static String[] token = {"MetaFile{ hot=", " order=", " id=", " size=", " key='", " name='", " userid='", " " +
            "Refrenceid='"};

    public MetaFile(int order, String userid, String key, String name) {
        this.order = order;
        this.userid = userid;
        this.key = key;
        this.name = name;
        this.hot = false;
    }

    public MetaFile(int order, int size, String name, String key, String userid, String Refrenceid, boolean hot) {
        this.order = order;
        this.userid = userid;
            this.key = key;
        this.name = name;
        this.hot = hot;
        this.size = size;
        this.Refrenceid = Refrenceid;
    }

    public MetaFile(String key, int[] counters) {
        this.counters = counters;
        this.key = key;
        this.hot = true;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setid(int id) {
        this.id = id;
    }

    public void setsize(int size) {
        this.size = size;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setRefrenceid(String Refrenceid) {
        this.Refrenceid = Refrenceid;
    }

    public void sethot(boolean hot) {
        this.hot = hot;
    }

    public boolean ishot() {
        return hot;
    }

    public int getOrder() {
        return order;
    }

    public int getsize() {
        return size;
    }

    public int getid() {
        return id;
    }

    public String getname() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getuserid() {
        return userid;
    }

    public String getRefrenceid() {
        return Refrenceid;
    }


    public String toStringMeta() {
        return "MetaFile{" +
                "hot=" + hot +
                ", name=" + name +
                ", key='" + key + '\'' +
                '}';
    }

    public String toStringBlock() {
        return "MetaFile{" +
                "order=" + order +
                ", size=" + size +
                ", key='" + key + '\'' +
                ", order='" + order + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "MetaFile{" +
                " hot=" + hot +
                ", order=" + order +
                ", id=" + id +
                ", size=" + size +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", userid='" + userid + '\'' +
                ", Refrenceid='" + Refrenceid + '\'' +
                '}';
    }

    public void Stringto(String metadata){
        String[] info = new String[8];
        for(int i = 0; i <8; i++)
        {
            info[i] = metadata.split(",")[i];
        }
        this.sethot(Boolean.parseBoolean(info[0].substring(token[0].length())));
        this.setOrder(Integer.parseInt(info[1].substring(token[1].length())));
        this.setid(Integer.parseInt(info[2].substring(token[2].length())));
        this.setsize(Integer.parseInt(info[3].substring(token[3].length())));
        this.setKey(info[4].substring(token[4].length(),info[4].lastIndexOf("\'")));
        this.setname(info[5].substring(token[5].length(),info[5].lastIndexOf("\'")));
        this.setUserid(info[6].substring(token[6].length(),info[6].lastIndexOf("\'")));
        this.setRefrenceid(info[7].substring(token[7].length(),info[7].lastIndexOf("\'")));
    }


}