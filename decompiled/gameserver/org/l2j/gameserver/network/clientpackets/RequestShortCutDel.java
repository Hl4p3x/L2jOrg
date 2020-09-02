// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.Shortcut;

public final class RequestShortCutDel extends ClientPacket
{
    private int room;
    
    public void readImpl() {
        this.room = this.readInt();
    }
    
    public void runImpl() {
        if (this.room < 0 || (this.room > 240 && this.room != Shortcut.AUTO_POTION_ROOM)) {
            return;
        }
        ((GameClient)this.client).getPlayer().deleteShortcut(this.room);
    }
}
