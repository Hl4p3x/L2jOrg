// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.ensoul;

import java.util.Iterator;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExEnsoulResult extends ServerPacket
{
    private final int _success;
    private final Item _item;
    
    public ExEnsoulResult(final int success, final Item item) {
        this._success = success;
        this._item = item;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENSOUL_RESULT);
        this.writeByte((byte)this._success);
        this.writeByte((byte)this._item.getSpecialAbilities().size());
        for (final EnsoulOption option : this._item.getSpecialAbilities()) {
            this.writeInt(option.getId());
        }
        this.writeByte((byte)this._item.getAdditionalSpecialAbilities().size());
        for (final EnsoulOption option : this._item.getAdditionalSpecialAbilities()) {
            this.writeInt(option.getId());
        }
    }
}
