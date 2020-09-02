// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.scripts.ai.AbstractNpcAI;

public class CastleSideEffect extends AbstractNpcAI
{
    private static final int[] ZONE_ID;
    
    public CastleSideEffect() {
        this.addEnterZoneId(CastleSideEffect.ZONE_ID);
    }
    
    public String onEnterZone(final Creature character, final Zone zone) {
        if (GameUtils.isPlayer((WorldObject)character)) {
            for (final Castle castle : CastleManager.getInstance().getCastles()) {
                character.sendPacket(new ServerPacket[] { (ServerPacket)new ExCastleState(castle) });
            }
        }
        return super.onEnterZone(character, zone);
    }
    
    public static void main(final String[] args) {
        new CastleSideEffect();
    }
    
    static {
        ZONE_ID = new int[] { 11020, 11027, 11028, 11029, 11031, 11032, 11033, 11034, 11035 };
    }
}
