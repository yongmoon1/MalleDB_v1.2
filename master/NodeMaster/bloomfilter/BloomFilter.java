package bloomfilter;

public interface BloomFilter {

    void addData(String data);

    boolean isPresent(String data);

    String getInfo();
}
