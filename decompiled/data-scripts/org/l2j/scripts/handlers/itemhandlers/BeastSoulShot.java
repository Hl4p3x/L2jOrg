// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.enums.ShotType;

public class BeastSoulShot extends AbstractBeastShot
{
    @Override
    protected ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }
    
    @Override
    protected boolean isBlessed() {
        return false;
    }
    
    @Override
    protected double getBonus(final Summon summon) {
        return summon.getStats().getValue(Stat.SOUL_SHOTS_BONUS, 1.0) * 2.0;
    }
    
    @Override
    protected void sendUsesMessage(final Player player) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1).addString("soulshot") });
    }
}
