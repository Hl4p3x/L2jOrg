// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestExMPCCShowPartyMembersInfo extends ClientPacket
{
    private int _partyLeaderId;
    
    public void readImpl() {
        this._partyLeaderId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player player = World.getInstance().findPlayer(this._partyLeaderId);
        if (player != null && player.getParty() != null) {
            ((GameClient)this.client).sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
        }
    }
}
