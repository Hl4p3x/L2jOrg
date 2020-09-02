// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class ConditionZone extends Zone
{
    private boolean NO_ITEM_DROP;
    private boolean NO_BOOKMARK;
    
    public ConditionZone(final int id) {
        super(id);
        this.NO_ITEM_DROP = false;
        this.NO_BOOKMARK = false;
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equalsIgnoreCase("NoBookmark")) {
            this.NO_BOOKMARK = Boolean.parseBoolean(value);
        }
        else if (name.equalsIgnoreCase("NoItemDrop")) {
            this.NO_ITEM_DROP = Boolean.parseBoolean(value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            if (this.NO_BOOKMARK) {
                character.setInsideZone(ZoneType.NO_BOOKMARK, true);
            }
            if (this.NO_ITEM_DROP) {
                character.setInsideZone(ZoneType.NO_ITEM_DROP, true);
            }
        }
    }
    
    @Override
    protected void onExit(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            if (this.NO_BOOKMARK) {
                character.setInsideZone(ZoneType.NO_BOOKMARK, false);
            }
            if (this.NO_ITEM_DROP) {
                character.setInsideZone(ZoneType.NO_ITEM_DROP, false);
            }
        }
    }
}
