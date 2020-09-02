// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.List;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class TowerSpawn implements IIdentifiable
{
    private final int _npcId;
    private final Location _location;
    private List<Integer> _zoneList;
    private int _upgradeLevel;
    
    public TowerSpawn(final int npcId, final Location location) {
        this._zoneList = null;
        this._upgradeLevel = 0;
        this._location = location;
        this._npcId = npcId;
    }
    
    public TowerSpawn(final int npcId, final Location location, final List<Integer> zoneList) {
        this._zoneList = null;
        this._upgradeLevel = 0;
        this._location = location;
        this._npcId = npcId;
        this._zoneList = zoneList;
    }
    
    @Override
    public int getId() {
        return this._npcId;
    }
    
    public Location getLocation() {
        return this._location;
    }
    
    public List<Integer> getZoneList() {
        return this._zoneList;
    }
    
    public int getUpgradeLevel() {
        return this._upgradeLevel;
    }
    
    public void setUpgradeLevel(final int level) {
        this._upgradeLevel = level;
    }
}
