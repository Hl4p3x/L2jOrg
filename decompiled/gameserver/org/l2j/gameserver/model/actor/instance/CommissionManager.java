// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.commission.ExShowCommission;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public class CommissionManager extends Npc
{
    public CommissionManager(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.CommissionManagerInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (command.equalsIgnoreCase("show_commission")) {
            player.sendPacket(ExShowCommission.STATIC_PACKET);
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
}
