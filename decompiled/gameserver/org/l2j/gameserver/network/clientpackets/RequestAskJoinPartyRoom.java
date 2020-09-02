// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public class RequestAskJoinPartyRoom extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player target = World.getInstance().findPlayer(this._name);
        if (target != null) {
            if (!target.isProcessingRequest()) {
                player.onTransactionRequest(target);
                target.sendPacket(new ExAskJoinPartyRoom(player));
            }
            else {
                player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER)).addPcName(target));
            }
        }
        else {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
        }
    }
}
