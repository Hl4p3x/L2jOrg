// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class TradeDone extends ClientPacket
{
    private int _response;
    
    public void readImpl() {
        this._response = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("trade")) {
            player.sendMessage("You are trading too fast.");
            return;
        }
        final TradeList trade = player.getActiveTradeList();
        if (trade == null) {
            return;
        }
        if (trade.isLocked()) {
            return;
        }
        if (this._response == 1) {
            if (trade.getPartner() == null || World.getInstance().findPlayer(trade.getPartner().getObjectId()) == null) {
                player.cancelActiveTrade();
                player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                return;
            }
            if (trade.getOwner().hasItemRequest() || trade.getPartner().hasItemRequest()) {
                return;
            }
            if (!player.getAccessLevel().allowTransaction()) {
                player.cancelActiveTrade();
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (player.getInstanceWorld() != trade.getPartner().getInstanceWorld()) {
                player.cancelActiveTrade();
                return;
            }
            if (!MathUtil.isInsideRadius3D(player, trade.getPartner(), 150)) {
                player.cancelActiveTrade();
                return;
            }
            trade.confirm();
        }
        else {
            player.cancelActiveTrade();
        }
    }
}
