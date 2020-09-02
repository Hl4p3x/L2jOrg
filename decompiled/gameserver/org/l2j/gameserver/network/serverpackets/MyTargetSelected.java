// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

public class MyTargetSelected extends ServerPacket
{
    private final int _objectId;
    private final int _color;
    
    public MyTargetSelected(final Player player, final Creature target) {
        this._objectId = target.getObjectId();
        this._color = (target.isAutoAttackable(player) ? (player.getLevel() - target.getLevel()) : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MY_TARGET_SELECTED);
        this.writeInt(1);
        this.writeInt(this._objectId);
        this.writeShort((short)this._color);
        this.writeInt(0);
    }
}
