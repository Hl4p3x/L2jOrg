// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Clan;
import io.github.joealisson.primitive.IntSet;
import java.util.Objects;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.util.PrimeFinder;
import java.util.BitSet;
import org.l2j.gameserver.data.database.data.CrestData;
import io.github.joealisson.primitive.IntMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public final class CrestTable
{
    private static final Logger LOGGER;
    private static final int INITIAL_CREST_SIZE = 100;
    private final AtomicInteger nextId;
    private IntMap<CrestData> crests;
    private BitSet crestIds;
    
    private CrestTable() {
        this.nextId = new AtomicInteger(1);
        this.crestIds = new BitSet(PrimeFinder.nextPrime(100));
        this.load();
    }
    
    public synchronized void load() {
        final ClanDAO clanDAO = (ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class);
        clanDAO.removeUnusedCrests();
        this.crests = clanDAO.findAllCrests();
        final IntSet keySet = this.crests.keySet();
        final BitSet crestIds = this.crestIds;
        Objects.requireNonNull(crestIds);
        keySet.forEach(crestIds::set);
        this.nextId.set(this.crestIds.nextClearBit(1));
        CrestTable.LOGGER.info("Loaded {} Crests.", (Object)this.crests.size());
    }
    
    public CrestData getCrest(final int crestId) {
        return (CrestData)this.crests.get(crestId);
    }
    
    public CrestData createCrest(final byte[] data, final CrestData.CrestType crestType) {
        final CrestData crest = new CrestData(this.getNextId(), data, crestType);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save(crest);
        this.crests.put(crest.getId(), (Object)crest);
        return crest;
    }
    
    private synchronized int getNextId() {
        final int id = this.nextId.get();
        int next = this.crestIds.nextClearBit(id + 1);
        if (next < 0) {
            next = this.crestIds.nextClearBit(1);
        }
        if (next < 0) {
            this.increaseCrests();
            next = this.crestIds.nextClearBit(id);
        }
        this.nextId.set(next);
        return id;
    }
    
    private void increaseCrests() {
        final BitSet expanded = new BitSet((int)(this.crestIds.size() * 1.5));
        expanded.or(this.crestIds);
        this.crestIds = expanded;
    }
    
    public void removeCrest(final int crestId) {
        if (Objects.nonNull(this.crests.remove(crestId))) {
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).deleteCrest(crestId);
        }
    }
    
    public void removeCrests(final Clan clan) {
        this.removeCrest(clan.getCrestId());
        this.removeCrest(clan.getCrestLargeId());
    }
    
    public static CrestTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CrestTable.class);
    }
    
    private static class Singleton
    {
        private static final CrestTable INSTANCE;
        
        static {
            INSTANCE = new CrestTable();
        }
    }
}
