// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.idfactory;

import org.l2j.gameserver.data.database.dao.IdFactoryDAO;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class IdFactory
{
    protected final Logger LOGGER;
    static final int FIRST_OID = 1;
    private static final int LAST_OID = Integer.MAX_VALUE;
    static final int FREE_OBJECT_ID_SIZE = 2147483646;
    boolean initialized;
    
    protected IdFactory() {
        this.LOGGER = LoggerFactory.getLogger(this.getClass().getName());
        this.cleanUpDatabase();
        this.cleanUpTimeStamps();
    }
    
    private void cleanUpDatabase() {
        final long cleanupStart = System.currentTimeMillis();
        int cleanCount = 0;
        cleanCount += ((AccountDAO)DatabaseAccess.getDAO((Class)AccountDAO.class)).deleteWithoutAccount();
        final ItemDAO itemDAO = (ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class);
        cleanCount += itemDAO.deleteWithoutOwner();
        cleanCount += itemDAO.deleteFromEmailWithoutMessage();
        final ClanDAO clanDao = (ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class);
        cleanCount += clanDao.deleteWithoutMembers();
        clanDao.resetAuctionBidWithoutAction();
        clanDao.resetNewLeaderWithoutCharacter();
        clanDao.resetSubpledgeLeaderWithoutCharacter();
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).updateToNeutralWithoutOwner();
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).resetClanInfoOfNonexistentClan();
        this.LOGGER.info("Cleaned {} elements from database in {} s", (Object)cleanCount, (Object)((System.currentTimeMillis() - cleanupStart) / 1000L));
    }
    
    private void cleanUpTimeStamps() {
        final long timestamp = System.currentTimeMillis();
        final PlayerDAO characterDAO = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        characterDAO.deleteExpiredInstances(timestamp);
        characterDAO.deleteExpiredSavedSkills(timestamp);
    }
    
    protected final IntSet extractUsedObjectIDTable() {
        return ((IdFactoryDAO)DatabaseAccess.getDAO((Class)IdFactoryDAO.class)).findUsedObjectIds();
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public abstract int getNextId();
    
    public abstract void releaseId(final int id);
    
    public abstract int size();
    
    public static IdFactory getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final IdFactory INSTANCE;
        
        static {
            INSTANCE = new BitSetIDFactory();
        }
    }
}
