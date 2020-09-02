// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.List;
import org.l2j.gameserver.model.actor.Tower;

public class FlameTower extends Tower
{
    private int _upgradeLevel;
    private List<Integer> _zoneList;
    
    public FlameTower(final NpcTemplate template) {
        super(template);
        this._upgradeLevel = 0;
        this.setInstanceType(InstanceType.L2FlameTowerInstance);
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        this.enableZones(false);
        return super.doDie(killer);
    }
    
    @Override
    public boolean deleteMe() {
        this.enableZones(false);
        return super.deleteMe();
    }
    
    public final void enableZones(final boolean state) {
        if (this._zoneList != null && this._upgradeLevel != 0) {
            for (int maxIndex = this._upgradeLevel * 2, i = 0; i < maxIndex; ++i) {
                final Zone zone = ZoneManager.getInstance().getZoneById(this._zoneList.get(i));
                if (zone != null) {
                    zone.setEnabled(state);
                }
            }
        }
    }
    
    public final void setUpgradeLevel(final int level) {
        this._upgradeLevel = level;
    }
    
    public final void setZoneList(final List<Integer> list) {
        this._zoneList = list;
        this.enableZones(true);
    }
}
