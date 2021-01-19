package bloomfilter;

import bloomfilter.hash.Murmur3;

public class BasicBloomFilter extends AbstractBloomFilter {

    public BasicBloomFilter(int size) {
        super(size);
    }

    public BasicBloomFilter(double falsePosProbability, int expectedElements) {
        super(falsePosProbability, expectedElements);
    }

    @Override
    int[] getHashedIndexForBitSet(String data) {
        return new int[]{Math.abs(data.hashCode()) % bitSet.size(), Math.abs(Murmur3.hash32(data.getBytes())) % bitSet.size(), };
    }


}
