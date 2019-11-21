package datastructures;

import com.google.common.hash.Hashing;

import java.util.stream.IntStream;

/**
 * This implementation uses MurmerHash3 for creating
 * the Bloom filter.
 */
public class BloomFilterImpl implements BloomFilter {

    private boolean[] bitArray;
    private int size;
    private int count;
    private double probability;
    private int hashCount;

    public BloomFilterImpl(int count, double probability) {
        this.count = count;
        this.probability = probability;

        this.size = getSize(count, probability);

        this.bitArray = new boolean[this.size];
        this.hashCount = getHashFunctionsCount(this.size, this.count);
    }

    @Override
    public void add(int item) {
        IntStream.range(1, this.hashCount + 1)
                .map(seed -> addItemForSeed(item, seed))
                .forEach(hashValue -> this.bitArray[hashValue % this.size] = true);
    }

    private int addItemForSeed(int item, int seed) {
        return Hashing.murmur3_32(seed)
                .hashInt(item)
                .asInt();
    }

    @Override
    public boolean contains(int item) {
        return IntStream.range(1, this.hashCount + 1)
                .map(seed -> addItemForSeed(item, seed))
                .allMatch(hashValue -> this.bitArray[hashValue % this.size]);
    }

    /**
     * Returns Size m = -(n * lg(p)) / (lg(2)^2)
     *
     * @param count       n number of elements
     * @param probability
     * @return size
     */
    private int getSize(int count, double probability) {
        double m = -(count * Math.log(probability)
                / Math.pow(Math.log(probability), 2));
        return (int) m;
    }

    /**
     * Returns number of hash functions(k) using formula
     * k = (m/n) * lg(2)
     *
     * @param size  m
     * @param count n
     * @return
     */
    private int getHashFunctionsCount(int size, int count) {
        return (int) ((size / count) * Math.log(2));
    }
}
