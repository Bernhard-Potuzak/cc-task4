package com.koordinator.app1;

public class FNVHash implements HashFunction{
    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;
    @Override
    public long hash(byte[] key) {
        long rv = FNV_64_INIT;
        final int len = key.length;
        for(int i = 0; i < len; i++) {
            rv ^= key[i];
            rv *= FNV_64_PRIME;
        }
        return rv;
    }
}
