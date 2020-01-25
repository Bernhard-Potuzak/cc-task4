package com.koordinator.app1;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashClass<T> {

    private final FNVHash hashFunction;
    private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public HashClass(FNVHash hashFunction, int numberOfReplicas,
                          Collection<T> nodes) {
        this.hashFunction = hashFunction;

    }

}
