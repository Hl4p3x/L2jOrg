// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;

public class ExShowContactList extends ServerPacket
{
    private final Set<String> _contacts;
    
    public ExShowContactList(final Player player) {
        this._contacts = player.getContactList().getAllContacts();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ADD_POST_FRIEND);
        this.writeInt(this._contacts.size());
        this._contacts.forEach(contact -> this.writeString((CharSequence)contact));
    }
}
