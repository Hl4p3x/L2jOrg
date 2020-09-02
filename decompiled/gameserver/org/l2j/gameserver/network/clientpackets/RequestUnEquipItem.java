// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.network.GameClient;

public class RequestUnEquipItem extends ClientPacket
{
    private int slot;
    
    public void readImpl() {
        this.slot = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final BodyPart bodyPart = BodyPart.fromSlot(this.slot);
        final Item item = player.getInventory().getItemByBodyPart(bodyPart);
        if (item == null) {
            return;
        }
        if (player.isAttackingNow() || player.isCastingNow()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK);
            return;
        }
        if (bodyPart == BodyPart.LEFT_HAND && item.getTemplate() instanceof EtcItem) {
            return;
        }
        if (player.hasBlockActions() || player.isAlikeDead()) {
            return;
        }
        if (player.getInventory().isBlocked(item)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }
        final Set<Item> modified = player.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
        player.broadcastUserInfo();
        final Iterator<Item> iterator = modified.iterator();
        if (iterator.hasNext()) {
            final InventoryUpdate iu = new InventoryUpdate(modified);
            player.sendInventoryUpdate(iu);
            final Item unequipped = iterator.next();
            SystemMessage sm;
            if (unequipped.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
            }
            sm.addItemName(unequipped);
            ((GameClient)this.client).sendPacket(sm);
        }
    }
}
