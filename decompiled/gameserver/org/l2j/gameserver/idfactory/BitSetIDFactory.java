// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.idfactory;

import org.l2j.gameserver.util.PrimeFinder;
import org.l2j.commons.threading.ThreadPool;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.BitSet;

public final class BitSetIDFactory extends IdFactory
{
    private static final int INITIAL_CAPACITY = 100000;
    private BitSet freeIds;
    private AtomicInteger freeIdCount;
    private AtomicInteger nextFreeId;
    
    BitSetIDFactory() {
        synchronized (BitSetIDFactory.class) {
            this.initialize();
            ThreadPool.scheduleAtFixedRate((Runnable)new BitSetCapacityCheck(), ChronoUnit.HOURS.getDuration(), ChronoUnit.HOURS.getDuration());
        }
        this.LOGGER.info("{} Identifiers available", (Object)this.freeIds.size());
    }
    
    public void initialize() {
        try {
            this.freeIds = new BitSet(PrimeFinder.nextPrime(100000));
            this.freeIdCount = new AtomicInteger(2147483646);
            final int objectID;
            this.extractUsedObjectIDTable().forEach(usedObjectId -> {
                objectID = usedObjectId - 1;
                if (objectID < 0) {
                    this.LOGGER.warn("Object ID {} in DB is less than minimum ID of {}", (Object)usedObjectId, (Object)1);
                    return;
                }
                else {
                    this.freeIds.set(usedObjectId - 1);
                    this.freeIdCount.decrementAndGet();
                    return;
                }
            });
            this.nextFreeId = new AtomicInteger(this.freeIds.nextClearBit(0));
            this.initialized = true;
        }
        catch (Exception e) {
            this.initialized = false;
            this.LOGGER.error("Could not be initialized properly", (Throwable)e);
        }
    }
    
    @Override
    public synchronized void releaseId(final int objectId) {
        if (objectId - 1 > -1) {
            this.freeIds.clear(objectId - 1);
            this.freeIdCount.incrementAndGet();
        }
        else {
            this.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, objectId));
        }
    }
    
    @Override
    public synchronized int getNextId() {
        final int newObjectId = this.nextFreeId.get();
        this.freeIds.set(newObjectId);
        this.freeIdCount.decrementAndGet();
        int nextFree = this.freeIds.nextClearBit(newObjectId);
        if (nextFree < 0) {
            nextFree = this.freeIds.nextClearBit(0);
        }
        if (nextFree < 0) {
            if (this.freeIds.size() >= 2147483646) {
                throw new IllegalStateException("Ran out of valid Id's.");
            }
            this.increaseBitSetCapacity();
        }
        this.nextFreeId.set(nextFree);
        return newObjectId + 1;
    }
    
    @Override
    public synchronized int size() {
        return this.freeIdCount.get();
    }
    
    private synchronized int usedIdCount() {
        return this.freeIdCount.get() - 1;
    }
    
    private synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10) > this.freeIds.size();
    }
    
    private synchronized void increaseBitSetCapacity() {
        final BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10));
        newBitSet.or(this.freeIds);
        this.freeIds = newBitSet;
    }
    
    protected class BitSetCapacityCheck implements Runnable
    {
        @Override
        public void run() {
            synchronized (BitSetIDFactory.this) {
                if (BitSetIDFactory.this.reachingBitSetCapacity()) {
                    BitSetIDFactory.this.increaseBitSetCapacity();
                }
            }
        }
    }
}
