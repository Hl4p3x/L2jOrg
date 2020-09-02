// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;

public final class RequestSaveBookMarkSlot extends ClientPacket
{
    private int icon;
    private String name;
    private String tag;
    
    public void readImpl() {
        this.name = this.readString();
        this.icon = this.readInt();
        this.tag = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.teleportBookmarkAdd(activeChar.getX(), activeChar.getY(), activeChar.getZ(), this.icon, this.tag, this.name);
    }
}
