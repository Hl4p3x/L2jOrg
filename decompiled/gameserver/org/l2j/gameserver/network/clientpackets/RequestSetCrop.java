// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Seed;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.CropProcure;
import java.util.List;

public final class RequestSetCrop extends ClientPacket
{
    private static final int BATCH_LENGTH = 21;
    private int _manorId;
    private List<CropProcure> _items;
    
    public void readImpl() throws InvalidDataPacketException {
        this._manorId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 21 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<CropProcure>(count);
        for (int i = 0; i < count; ++i) {
            final int itemId = this.readInt();
            final long sales = this.readLong();
            final long price = this.readLong();
            final int type = this.readByte();
            if (itemId < 1 || sales < 0L || price < 0L) {
                this._items.clear();
                throw new InvalidDataPacketException();
            }
            if (sales > 0L) {
                this._items.add(new CropProcure(itemId, sales, type, sales, price, this._manorId, true));
            }
        }
    }
    
    public void runImpl() {
        if (this._items.isEmpty()) {
            return;
        }
        final CastleManorManager manor = CastleManorManager.getInstance();
        if (!manor.isModifiablePeriod()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClan() == null || player.getClan().getCastleId() != this._manorId || !player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN) || !player.getLastFolkNPC().canInteract(player)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final List<CropProcure> list = new ArrayList<CropProcure>(this._items.size());
        for (final CropProcure cp : this._items) {
            final Seed s = manor.getSeedByCrop(cp.getSeedId(), this._manorId);
            if (s != null && cp.getStartAmount() <= s.getCropLimit() && cp.getPrice() >= s.getCropMinPrice() && cp.getPrice() <= s.getCropMaxPrice()) {
                list.add(cp);
            }
        }
        manor.setNextCropProcure(list, this._manorId);
    }
}
