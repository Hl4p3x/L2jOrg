// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.TeleportBookmark;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExGetBookMarkInfoPacket extends ServerPacket
{
    private final Player player;
    
    public ExGetBookMarkInfoPacket(final Player cha) {
        this.player = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_BOOKMARK);
        this.writeInt(0);
        this.writeInt(this.player.getBookmarkslot());
        this.writeInt(this.player.getTeleportBookmarks().size());
        for (final TeleportBookmark tpbm : this.player.getTeleportBookmarks()) {
            this.writeInt(tpbm.getId());
            this.writeInt(tpbm.getX());
            this.writeInt(tpbm.getY());
            this.writeInt(tpbm.getZ());
            this.writeString((CharSequence)tpbm.getName());
            this.writeInt(tpbm.getIcon());
            this.writeString((CharSequence)tpbm.getTag());
        }
    }
}
