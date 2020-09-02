// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.AskJoinPledge;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;

public final class RequestJoinPledge extends ClientPacket
{
    private int _target;
    private int _pledgeType;
    
    public void readImpl() {
        this._target = this.readInt();
        this._pledgeType = this.readInt();
    }
    
    private void scheduleDeny(final Player player, final String name) {
        if (player != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }
        final Player target = World.getInstance().findPlayer(this._target);
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }
        if (!clan.checkClanJoinCondition(activeChar, target, this._pledgeType)) {
            return;
        }
        if (!activeChar.getRequest().setRequest(target, this)) {
            return;
        }
        final String pledgeName = activeChar.getClan().getName();
        target.sendPacket(new AskJoinPledge(activeChar, this._pledgeType, pledgeName));
    }
    
    public int getPledgeType() {
        return this._pledgeType;
    }
}
