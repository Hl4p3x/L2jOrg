// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestDivideAdenaCancel extends ClientPacket
{
    private boolean _cancel;
    
    public void readImpl() {
        this._cancel = (this.readByte() == 0);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._cancel) {
            final AdenaDistributionRequest request = player.getRequest(AdenaDistributionRequest.class);
            request.getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
                p.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
                p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
                p.removeRequest(AdenaDistributionRequest.class);
            });
        }
    }
}
