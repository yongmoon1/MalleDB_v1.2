package bloomfilter;

import java.util.Arrays;
import java.util.BitSet;

public abstract class AbstractBloomFilter implements BloomFilter {

    protected BitSet bitSet;
    protected int expectedElements = -1;    //How many elements are going to be in the DB
    protected double falsePosProbability = -1;  //How much false positives can we tolerate
    protected int optimumHashFunctions = -1;

    public AbstractBloomFilter(int size) {
        this.bitSet = new BitSet(size);
    }

    public AbstractBloomFilter(double falsePosProbability, int expectedElements) {
        int size = (-1 * (expectedElements * log2(falsePosProbability) / (log2(2) * log2(2))));
        int fnCount = ((size / expectedElements) * log2(2));
        this.bitSet = new BitSet(size);
        this.expectedElements = expectedElements;
        this.falsePosProbability = falsePosProbability;
        this.optimumHashFunctions = fnCount;
    }


    @Override
    public void addData(String data) {
        Arrays.stream(getHashedIndexForBitSet(data)).forEach(bitSet::set);
    }

    @Override
    public boolean isPresent(String data) {
        return Arrays.stream(getHashedIndexForBitSet(data)).allMatch(bitSet::get);
    }

    @Override
    public String getInfo() {
        return "\n BloomFilter :: \n\tsize :: " + bitSet.size() + "\n\tfalsePosProbability :: " + falsePosProbability
                + "\n\texpectedElements :: " + expectedElements
                + "\n\toptimumHashFunctions :: " + optimumHashFunctions + "\n";
    }

    abstract int[] getHashedIndexForBitSet(String data);

    public static int log2(double f) {
        return (int) Math.floor(Math.log(f) / Math.log(2.0));
    }
}
