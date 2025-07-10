package idc.inbound.secure;

import io.github.bucket4j.Bucket;
import lombok.Getter;

@Getter
public class BucketWrapper {

    private final Bucket bucket;
    private volatile long lastAccessed;

    public BucketWrapper(Bucket bucket) {
        this.bucket = bucket;
        this.lastAccessed = System.currentTimeMillis();
    }

    public void updateLastAccessed() {
        this.lastAccessed = System.currentTimeMillis();
    }
}
