// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestHennaEquip extends ClientPacket
{
    private static final Logger LOGGER;
    private int _symbolId;
    
    public void readImpl() {
        this._symbolId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("HennaEquip")) {
            return;
        }
        if (activeChar.getHennaEmptySlots() == 0) {
            activeChar.sendPacket(SystemMessageId.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Henna henna = HennaData.getInstance().getHenna(this._symbolId);
        if (henna == null) {
            RequestHennaEquip.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this._symbolId, activeChar));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final long _count = activeChar.getInventory().getInventoryItemCount(henna.getDyeItemId(), -1);
        if (henna.isAllowedClass(activeChar.getClassId()) && _count >= henna.getWearCount() && activeChar.getAdena() >= henna.getWearFee() && activeChar.addHenna(henna)) {
            activeChar.destroyItemByItemId("Henna", henna.getDyeItemId(), henna.getWearCount(), activeChar, true);
            activeChar.getInventory().reduceAdena("Henna", henna.getWearFee(), activeChar, activeChar.getLastFolkNPC());
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(activeChar.getInventory().getAdenaInstance());
            activeChar.sendInventoryUpdate(iu);
            activeChar.sendPacket(new HennaEquipList(activeChar));
            activeChar.sendPacket(SystemMessageId.THE_SYMBOL_HAS_BEEN_ADDED);
        }
        else {
            activeChar.sendPacket(SystemMessageId.THE_SYMBOL_CANNOT_BE_DRAWN);
            if (!activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !henna.isAllowedClass(activeChar.getClassId())) {
                GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName()));
            }
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestHennaEquip.class);
    }
}
