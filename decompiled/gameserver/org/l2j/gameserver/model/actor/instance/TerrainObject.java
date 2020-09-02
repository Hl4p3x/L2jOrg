// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public final class TerrainObject extends Npc
{
    public TerrainObject(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2TerrainObjectInstance);
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public void onActionShift(final Player player) {
        if (player.isGM()) {
            super.onActionShift(player);
        }
        else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
