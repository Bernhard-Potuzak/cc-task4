package com.koordinator.app1;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashClass<T> {

    private final FNVHash hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public HashClass(FNVHash hashFunction, int numberOfReplicas,
                          Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add() {
        for (int i = 0; i <numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.getBytes() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i <numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.getBytes() + i));
        }
    }

    public T get(byte key) {
        if (circle.isEmpty()) {
            return null;
        }
        byte[] hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
}
