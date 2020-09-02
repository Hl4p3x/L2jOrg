// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.DamageInfo;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;

public class WaterTask implements Runnable
{
    private final Player player;
    
    public WaterTask(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        if (Objects.nonNull(this.player)) {
            final double reduceHp = Math.min(1.0, this.player.getMaxHp() / 100.0);
            this.player.reduceCurrentHp(reduceHp, null, null, false, true, false, false, DamageInfo.DamageType.DROWN);
            this.player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_TAKEN_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE)).addInt((int)reduceHp));
        }
    }
}
