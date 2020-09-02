// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.item.Henna;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;

public final class GMHennaInfo extends ServerPacket
{
    private final Player _activeChar;
    private final List<Henna> _hennas;
    
    public GMHennaInfo(final Player player) {
        this._hennas = new ArrayList<Henna>();
        this._activeChar = player;
        for (final Henna henna : this._activeChar.getHennaList()) {
            if (henna != null) {
                this._hennas.add(henna);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_HENNA_INFO);
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.INT));
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.STR));
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.CON));
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.MEN));
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.DEX));
        this.writeShort((short)this._activeChar.getHennaValue(BaseStats.WIT));
        this.writeShort((short)0);
        this.writeShort((short)0);
        this.writeInt(3);
        this.writeInt(this._hennas.size());
        for (final Henna henna : this._hennas) {
            this.writeInt(henna.getDyeId());
            this.writeInt(1);
        }
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
