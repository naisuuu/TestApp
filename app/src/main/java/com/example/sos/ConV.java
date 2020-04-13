package com.example.sos;

public class ConV {

    public boolean seen;
    public long timestamp;

    public ConV(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ConV(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }
}
