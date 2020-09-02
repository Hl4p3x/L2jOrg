// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowContactList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;

public final class RequestExShowContactList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExShowContactList(activeChar));
    }
}
