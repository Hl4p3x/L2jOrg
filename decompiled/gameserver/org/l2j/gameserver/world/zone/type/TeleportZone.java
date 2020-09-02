// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class TeleportZone extends Zone
{
    private int x;
    private int y;
    private int z;
    
    public TeleportZone(final int id) {
        super(id);
        this.x = -1;
        this.y = -1;
        this.z = -1;
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        switch (name) {
            case "oustX": {
                this.x = Integer.parseInt(value);
                break;
            }
            case "oustY": {
                this.y = Integer.parseInt(value);
                break;
            }
            case "oustZ": {
                this.z = Integer.parseInt(value);
                break;
            }
            default: {
                super.setParameter(name, value);
                break;
            }
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.teleToLocation(new Location(this.x, this.y, this.z));
    }
    
    @Override
    protected void onExit(final Creature creature) {
    }
}
