// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;

public final class HennaItemRemoveInfo extends ServerPacket
{
    private final Player _activeChar;
    private final Henna _henna;
    
    public HennaItemRemoveInfo(final Henna henna, final Player player) {
        this._henna = henna;
        this._activeChar = player;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.HENNA_UNEQUIP_INFO);
        this.writeInt(this._henna.getDyeId());
        this.writeInt(this._henna.getDyeItemId());
        this.writeLong((long)this._henna.getCancelCount());
        this.writeLong((long)this._henna.getCancelFee());
        this.writeInt((int)(this._henna.isAllowedClass(this._activeChar.getClassId()) ? 1 : 0));
        this.writeLong(this._activeChar.getAdena());
        this.writeInt(this._activeChar.getINT());
        this.writeShort((short)(this._activeChar.getINT() - this._activeChar.getHennaValue(BaseStats.INT)));
        this.writeInt(this._activeChar.getSTR());
        this.writeShort((short)(this._activeChar.getSTR() - this._activeChar.getHennaValue(BaseStats.STR)));
        this.writeInt(this._activeChar.getCON());
        this.writeShort((short)(this._activeChar.getCON() - this._activeChar.getHennaValue(BaseStats.CON)));
        this.writeInt(this._activeChar.getMEN());
        this.writeShort((short)(this._activeChar.getMEN() - this._activeChar.getHennaValue(BaseStats.MEN)));
        this.writeInt(this._activeChar.getDEX());
        this.writeShort((short)(this._activeChar.getDEX() - this._activeChar.getHennaValue(BaseStats.DEX)));
        this.writeInt(this._activeChar.getWIT());
        this.writeShort((short)(this._activeChar.getWIT() - this._activeChar.getHennaValue(BaseStats.WIT)));
        this.writeInt(0);
        this.writeShort((short)0);
        this.writeInt(0);
        this.writeShort((short)0);
        this.writeInt(0);
    }
}
