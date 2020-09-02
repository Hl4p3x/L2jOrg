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
import org.l2j.gameserver.data.database.data.SeedProduction;
import java.util.List;

public class RequestSetSeed extends ClientPacket
{
    private static final int BATCH_LENGTH = 20;
    private int _manorId;
    private List<SeedProduction> _items;
    
    public void readImpl() throws InvalidDataPacketException {
        this._manorId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 20 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<SeedProduction>(count);
        for (int i = 0; i < count; ++i) {
            final int itemId = this.readInt();
            final long sales = this.readLong();
            final long price = this.readLong();
            if (itemId < 1 || sales < 0L || price < 0L) {
                this._items.clear();
                throw new InvalidDataPacketException();
            }
            if (sales > 0L) {
                this._items.add(new SeedProduction(itemId, sales, price, sales, this._manorId, true));
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
        final List<SeedProduction> list = new ArrayList<SeedProduction>(this._items.size());
        for (final SeedProduction sp : this._items) {
            final Seed s = manor.getSeed(sp.getSeedId());
            if (s != null && sp.getStartAmount() <= s.getSeedLimit() && sp.getPrice() >= s.getSeedMinPrice() && sp.getPrice() <= s.getSeedMaxPrice()) {
                list.add(sp);
            }
        }
        manor.setNextSeedProduction(list, this._manorId);
    }
}
