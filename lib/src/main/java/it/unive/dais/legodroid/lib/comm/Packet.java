package it.unive.dais.legodroid.lib.comm;

import android.support.annotation.NonNull;

public abstract class Packet {
    protected final int counter;
    @NonNull
    protected final byte[] data;

    protected Packet(int counter, @NonNull byte[] data) {
        this.counter = counter;
        this.data = data;
    }

    public int getCounter() {
        return counter;
    }

    @NonNull
    public byte[] getData() { return data; }

}
