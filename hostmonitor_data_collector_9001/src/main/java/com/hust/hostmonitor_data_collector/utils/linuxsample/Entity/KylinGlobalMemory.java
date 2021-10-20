package com.hust.hostmonitor_data_collector.utils.linuxsample.Entity;

public class KylinGlobalMemory {
    private long total;
    private long available;

    public KylinGlobalMemory(long total, long available) {
        this.total = total;
        this.available = available;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }
}
