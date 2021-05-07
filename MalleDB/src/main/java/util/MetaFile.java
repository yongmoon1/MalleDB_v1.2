package util;

import java.text.SimpleDateFormat;
import java.lang.String;
import java.util.Date;

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
    private String metaID;
    private int size;
    private String name;
    private String key;
    private String userid;
    private String Refrenceid;
    private String time;
    private MetaFile link;
    private int linknum = 0;

    private boolean hot;
    private boolean isBackup;
    private boolean isinMemory;

    private int isBig;
    private boolean isAPI;
    private int n;
    private String metaListId;


    public MetaFile() {
        this.key = "eeeeeee";
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

    public MetaFile(int size, String name, int isBig, int n) {
        this.size = size;
        this.name = name;

        // MetaID 와 Time은 알아서 생성
        Date dateNow = new Date(System.currentTimeMillis()); // 현재시간을 가져와 Date형으로 저장한다
        SimpleDateFormat fourteenFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = fourteenFormat.format(dateNow);
        this.time = time;
        this.metaID = time + name;

        this.isBig = isBig;
        this.n = n;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    public void setid(String id) {
        this.metaID = id;
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
    public void setTime(String time) {
        this.time = time;
    }
    public void setAPI(boolean API) { isAPI = API; }
    public void setMetaListId(String metaListId) {
        this.metaListId = metaListId;
    }
    public void setBig(int big) { isBig = big; }
    public void setN(int n) { this.n = n; }
    public void setLink(MetaFile link){
        this.link = link;
        this.linknum = linknum + 1;
    }

    public int getOrder() {
        return order;
    }
    public int getsize() {
        return size;
    }
    public String getid() {
        return metaID;
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
    public int getN() {
        return n;
    }
    public String getMetaListId() {
        return metaListId;
    }
    public String getTime() {
        return time;
    }
    public MetaFile getLink(){ return this.link; }

    public  boolean isAPI() { return isAPI; }
    public int isBig() {
        return isBig;
    }
    public boolean ishot() { return hot; }





    public static String[] token = {"MetaFile{ hot=", " order=", " id=", " size=", " key='", " name='", " userid='", " " +
            "Refrenceid='", " time='", " isBig='", " n='", " metaListId='", " isAPI='"};

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
                ", id=" + metaID +
                ", size=" + size +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", userid='" + userid + '\'' +
                ", Refrenceid='" + Refrenceid + '\'' +
                ", time='" + time + '\'' +
                ", isBig='" + isBig + '\'' +
                ", n='" + n + '\'' +
                ", metaListId='" + metaListId + '\'' +
                ", isAPI='" + isAPI + '\'' +
                '}';
    }

    public void Stringto(String metadata) {
        String[] info = new String[13];
        for (int i = 0; i < 13; i++) {
            info[i] = metadata.split(",")[i];
        }
        this.sethot(Boolean.parseBoolean(info[0].substring(token[0].length())));
        this.setOrder(Integer.parseInt(info[1].substring(token[1].length())));
        this.setid(info[2].substring(token[2].length()));
        this.setsize(Integer.parseInt(info[3].substring(token[3].length())));
        this.setKey(info[4].substring(token[4].length(), info[4].lastIndexOf("\'")));
        this.setname(info[5].substring(token[5].length(), info[5].lastIndexOf("\'")));
        this.setUserid(info[6].substring(token[6].length(), info[6].lastIndexOf("\'")));
        this.setRefrenceid(info[7].substring(token[7].length(), info[7].lastIndexOf("\'")));
        this.setTime(info[8].substring(token[8].length(), info[8].lastIndexOf('\'')));
        this.setBig(Integer.parseInt(info[9].substring(token[9].length(), info[9].lastIndexOf('\''))));
        this.setN(Integer.parseInt(info[10].substring(token[10].length(), info[10].lastIndexOf('\''))));
        this.setMetaListId(info[11].substring(token[11].length(), info[11].lastIndexOf('\'')));
        this.setAPI(Boolean.parseBoolean(info[12].substring(token[12].length(), info[12].lastIndexOf('\''))));
    }


}