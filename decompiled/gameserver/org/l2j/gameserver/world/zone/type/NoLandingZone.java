// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class NoLandingZone extends Zone
{
    private int dismountDelay;
    
    public NoLandingZone(final int id) {
        super(id);
        this.dismountDelay = 5;
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("dismountDelay")) {
            this.dismountDelay = Integer.parseInt(value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, true);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                creature.getActingPlayer().enteredNoLanding(this.dismountDelay);
            }
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, false);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.getActingPlayer().exitedNoLanding();
            }
        }
    }
}
