// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.primeshop;

import java.util.Iterator;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBRProductList extends ServerPacket
{
    private final Player _activeChar;
    private final int _type;
    private final Collection<PrimeShopProduct> _primeList;
    
    public ExBRProductList(final Player activeChar, final int type, final Collection<PrimeShopProduct> items) {
        this._activeChar = activeChar;
        this._type = type;
        this._primeList = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_PRODUCT_LIST_ACK);
        this.writeLong(this._activeChar.getAdena());
        this.writeLong(0L);
        this.writeByte((byte)this._type);
        this.writeInt(this._primeList.size());
        for (final PrimeShopProduct brItem : this._primeList) {
            this.writeInt(brItem.getId());
            this.writeByte(brItem.getCategory());
            this.writeByte(brItem.getPaymentType());
            this.writeInt(brItem.getPrice());
            this.writeByte(brItem.getPanelType());
            this.writeInt((int)brItem.getRecommended());
            this.writeInt(brItem.getStartSale());
            this.writeInt(brItem.getEndSale());
            this.writeByte(brItem.getDaysOfWeek());
            this.writeByte(brItem.getStartHour());
            this.writeByte(brItem.getStartMinute());
            this.writeByte(brItem.getStopHour());
            this.writeByte(brItem.getStopMinute());
            this.writeInt(brItem.getStock());
            this.writeInt((int)brItem.getTotal());
            this.writeByte(brItem.getSalePercent());
            this.writeByte(brItem.getMinLevel());
            this.writeByte(brItem.getMaxLevel());
            this.writeInt((int)brItem.getMinBirthday());
            this.writeInt((int)brItem.getMaxBirthday());
            this.writeInt((int)brItem.getRestrictionDay());
            this.writeInt((int)brItem.getAvailableCount());
            this.writeByte((byte)brItem.getItems().size());
            for (final PrimeShopItem item : brItem.getItems()) {
                this.writeInt(item.getId());
                this.writeInt((int)item.getCount());
                this.writeInt(item.getWeight());
                this.writeInt(item.isTradable());
            }
        }
    }
}
