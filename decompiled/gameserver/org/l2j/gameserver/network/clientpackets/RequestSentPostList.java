// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowSentPostList;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class RequestSentPostList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player) || !((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExShowSentPostList(player.getObjectId()));
    }
}
