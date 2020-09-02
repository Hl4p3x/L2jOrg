// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MobGroupTable
{
    public static final int FOLLOW_RANGE = 300;
    public static final int RANDOM_RANGE = 300;
    private final Map<Integer, MobGroup> _groupMap;
    
    private MobGroupTable() {
        this._groupMap = new ConcurrentHashMap<Integer, MobGroup>();
    }
    
    public void addGroup(final int groupKey, final MobGroup group) {
        this._groupMap.put(groupKey, group);
    }
    
    public MobGroup getGroup(final int groupKey) {
        return this._groupMap.get(groupKey);
    }
    
    public int getGroupCount() {
        return this._groupMap.size();
    }
    
    public MobGroup getGroupForMob(final ControllableMob mobInst) {
        for (final MobGroup mobGroup : this._groupMap.values()) {
            if (mobGroup.isGroupMember(mobInst)) {
                return mobGroup;
            }
        }
        return null;
    }
    
    public MobGroup[] getGroups() {
        return this._groupMap.values().toArray(new MobGroup[this._groupMap.size()]);
    }
    
    public boolean removeGroup(final int groupKey) {
        return this._groupMap.remove(groupKey) != null;
    }
    
    public static MobGroupTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        protected static final MobGroupTable INSTANCE;
        
        static {
            INSTANCE = new MobGroupTable();
        }
    }
}
