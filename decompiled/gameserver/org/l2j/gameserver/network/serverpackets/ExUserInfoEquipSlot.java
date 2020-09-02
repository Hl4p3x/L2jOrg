// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.InventorySlot;

public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot>
{
    private final Player player;
    private final byte[] masks;
    
    public ExUserInfoEquipSlot(final Player cha) {
        this(cha, true);
    }
    
    public ExUserInfoEquipSlot(final Player cha, final boolean addAll) {
        this.masks = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        this.player = cha;
        if (addAll) {
            this.addComponentType(InventorySlot.values());
        }
    }
    
    @Override
    protected byte[] getMasks() {
        return this.masks;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_INFO_EQUIPSLOT);
        this.writeInt(this.player.getObjectId());
        this.writeShort(InventorySlot.values().length);
        this.writeBytes(this.masks);
        final PlayerInventory inventory = this.player.getInventory();
        for (final InventorySlot slot : this.getPaperdollOrder()) {
            if (this.containsMask(slot)) {
                final VariationInstance augment = inventory.getPaperdollAugmentation(slot);
                this.writeShort(22);
                this.writeInt(inventory.getPaperdollObjectId(slot));
                this.writeInt(inventory.getPaperdollItemId(slot));
                this.writeInt(Util.zeroIfNullOrElse((Object)augment, VariationInstance::getOption1Id));
                this.writeInt(Util.zeroIfNullOrElse((Object)augment, VariationInstance::getOption2Id));
                this.writeInt(0);
            }
        }
    }
}
