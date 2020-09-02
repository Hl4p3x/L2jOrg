// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.entity.ClanHall;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;

public class ClanHallZone extends ResidenceZone
{
    public ClanHallZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("clanHallId")) {
            this.setResidenceId(Integer.parseInt(value));
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            character.setInsideZone(ZoneType.CLAN_HALL, true);
        }
    }
    
    @Override
    protected void onExit(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            character.setInsideZone(ZoneType.CLAN_HALL, false);
        }
    }
    
    @Override
    public final Location getBanishSpawnLoc() {
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(this.getResidenceId());
        return Objects.isNull(clanHall) ? null : clanHall.getBanishLocation();
    }
}
