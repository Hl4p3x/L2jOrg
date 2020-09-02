// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.TradeDone;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class AnswerTradeRequest extends ClientPacket
{
    private int response;
    
    public void readImpl() {
        this.response = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Player partner = player.getActiveRequester();
        if (Objects.isNull(partner) || Objects.isNull(World.getInstance().findPlayer(partner.getObjectId()))) {
            player.sendPacket(TradeDone.CANCELLED);
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
            player.setActiveRequester(null);
            return;
        }
        if (this.response == 1 && !partner.isRequestExpired()) {
            player.startTrade(partner);
        }
        else {
            partner.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE)).addString(player.getName()));
        }
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}
