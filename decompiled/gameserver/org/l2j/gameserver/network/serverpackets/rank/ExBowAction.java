// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBowAction extends ServerPacket
{
    private final int objectId;
    
    public ExBowAction(final Player ranker) {
        this.objectId = ranker.getObjectId();
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BOW_ACTION_TO);
        this.writeInt(this.objectId);
    }
}
