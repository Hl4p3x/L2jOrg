// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestActivateAutoShortcut extends ClientPacket
{
    private boolean activate;
    private int room;
    
    @Override
    protected void readImpl() {
        this.room = this.readShort();
        this.activate = this.readBoolean();
    }
    
    @Override
    protected void runImpl() {
        if (AutoPlayEngine.getInstance().setActiveAutoShortcut(((GameClient)this.client).getPlayer(), this.room, this.activate)) {
            ((GameClient)this.client).sendPacket(new ExActivateAutoShortcut(this.room, this.activate));
        }
        else {
            ((GameClient)this.client).sendPacket(new ExActivateAutoShortcut(this.room, false));
        }
    }
}
