package com.cebrail.botum.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Example usage:
 * DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();
 * drng.addNumber(1, 0.3d); // Adds the numerical value 1 with a probability of 0.3 (30%)
 * // [...] Add more values
 *
 * int random = drng.getDistributedRandomNumber(); // Generate a random number
 */
public class DistributedRandomNumberGenerator {

    private Map<Integer, Double> distribution;
    private double distSum;

    public DistributedRandomNumberGenerator() {
        distribution = new HashMap<>();
    }

    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public int getDistributedRandomNumber() {
        double rand = Math.random();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }
}