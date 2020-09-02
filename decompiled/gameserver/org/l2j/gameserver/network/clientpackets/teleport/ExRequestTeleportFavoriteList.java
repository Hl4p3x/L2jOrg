// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.teleport.ExTeleportFavoritesList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestTeleportFavoriteList extends ClientPacket
{
    @Override
    protected void readImpl() {
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExTeleportFavoritesList(true));
    }
}
