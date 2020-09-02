// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.crystalization;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.crystalization.ExGetCrystalizingEstimation;
import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCrystallizeEstimate extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private long _count;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._count = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.isInCrystallize()) {
            return;
        }
        if (this._count <= 0L) {
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this._objectId, activeChar.getName()));
            return;
        }
        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE || activeChar.isInCrystallize()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        final int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
        if (skillLevel <= 0) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (item == null || item.isTimeLimitedItem() || item.isHeroItem()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!item.getTemplate().isCrystallizable() || item.getTemplate().getCrystalCount() <= 0 || item.getTemplate().getCrystalType() == CrystalType.NONE) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            RequestCrystallizeEstimate.LOGGER.warn("{} tried to crystallize {}", (Object)activeChar, (Object)item.getTemplate());
            return;
        }
        if (this._count > item.getCount()) {
            this._count = activeChar.getInventory().getItemByObjectId(this._objectId).getCount();
        }
        if (activeChar.getInventory().isBlocked(item)) {
            activeChar.sendMessage("You cannot use this item.");
            return;
        }
        boolean canCrystallize = true;
        switch (item.getTemplate().getCrystalType()) {
            case D: {
                if (skillLevel < 1) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case C: {
                if (skillLevel < 2) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case B: {
                if (skillLevel < 3) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case A: {
                if (skillLevel < 4) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case S: {
                if (skillLevel < 5) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
        }
        if (!canCrystallize) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(item);
        if (crystallizationRewards != null && !crystallizationRewards.isEmpty()) {
            activeChar.setInCrystallize(true);
            ((GameClient)this.client).sendPacket(new ExGetCrystalizingEstimation(crystallizationRewards));
        }
        else {
            ((GameClient)this.client).sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestCrystallizeEstimate.class);
    }
}
