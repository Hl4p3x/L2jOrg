// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.ensoul;

import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ensoul.ExEnSoulExtractionResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestTryEnSoulExtraction extends ClientPacket
{
    private int _itemObjectId;
    private int _type;
    private int _position;
    
    public void readImpl() {
        this._itemObjectId = this.readInt();
        this._type = this.readByte();
        this._position = this.readByte() - 1;
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Item item = player.getInventory().getItemByObjectId(this._itemObjectId);
        if (item == null) {
            return;
        }
        EnsoulOption option = null;
        if (this._type == 1) {
            option = item.getSpecialAbility(this._position);
            if (option == null && this._position == 0) {
                option = item.getSpecialAbility(1);
                if (option != null) {
                    this._position = 1;
                }
            }
        }
        if (this._type == 2) {
            option = item.getAdditionalSpecialAbility(this._position);
        }
        if (option == null) {
            return;
        }
        final Collection<ItemHolder> removalFee = EnsoulData.getInstance().getRemovalFee(item.getTemplate().getCrystalType());
        if (removalFee.isEmpty()) {
            return;
        }
        for (final ItemHolder itemHolder : removalFee) {
            if (player.getInventory().getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount()) {
                player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
                player.sendPacket(new ExEnSoulExtractionResult(false, item));
                return;
            }
        }
        for (final ItemHolder itemHolder : removalFee) {
            player.destroyItemByItemId("Rune Extract", itemHolder.getId(), itemHolder.getCount(), player, true);
        }
        item.removeSpecialAbility(this._position, this._type);
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(item);
        final int runeId = EnsoulData.getInstance().getStone(this._type, option.getId());
        if (runeId > 0) {
            iu.addItem(player.addItem("Rune Extract", runeId, 1L, player, true));
        }
        player.sendInventoryUpdate(iu);
        player.sendPacket(new ExEnSoulExtractionResult(true, item));
    }
}
