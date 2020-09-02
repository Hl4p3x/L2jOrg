// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.cache;

import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;

public class WarehouseCacheManager
{
    private final Map<Player, Long> _cachedWh;
    private final long _cacheTime;
    
    private WarehouseCacheManager() {
        this._cachedWh = new ConcurrentHashMap<Player, Long>();
        this._cacheTime = Config.WAREHOUSE_CACHE_TIME * 60000;
        ThreadPool.scheduleAtFixedRate((Runnable)new CacheScheduler(), 120000L, 60000L);
    }
    
    public void addCacheTask(final Player pc) {
        this._cachedWh.put(pc, System.currentTimeMillis());
    }
    
    public void remCacheTask(final Player pc) {
        this._cachedWh.remove(pc);
    }
    
    public static WarehouseCacheManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final WarehouseCacheManager INSTANCE;
        
        static {
            INSTANCE = new WarehouseCacheManager();
        }
    }
    
    private class CacheScheduler implements Runnable
    {
        public CacheScheduler() {
        }
        
        @Override
        public void run() {
            final long cTime = System.currentTimeMillis();
            for (final Player pc : WarehouseCacheManager.this._cachedWh.keySet()) {
                if (cTime - WarehouseCacheManager.this._cachedWh.get(pc) > WarehouseCacheManager.this._cacheTime) {
                    pc.clearWarehouse();
                    WarehouseCacheManager.this._cachedWh.remove(pc);
                }
            }
        }
    }
}
