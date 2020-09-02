// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class KickPlayer extends ReceivablePacket
{
    private String account;
    
    public void readImpl() {
        this.account = this.readString();
    }
    
    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
        if (client == null) {
            client = AuthServerCommunication.getInstance().removeAuthedClient(this.account);
        }
        if (client == null) {
            return;
        }
        final Player activeChar = client.getPlayer();
        if (activeChar != null) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE);
            Disconnection.of(activeChar).defaultSequence(false);
        }
        else {
            client.close((WritablePacket)ServerClose.STATIC_PACKET);
        }
    }
}
