// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.ai.NextAction;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class UseItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int objectId;
    private boolean ctrlPressed;
    private int itemId;
    
    public void readImpl() {
        this.objectId = this.readInt();
        this.ctrlPressed = (this.readInt() != 0);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getUseItem().tryPerformAction("use item")) {
            return;
        }
        if (Objects.nonNull(player.getActiveTradeList())) {
            player.cancelActiveTrade();
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Item item = player.getInventory().getItemByObjectId(this.objectId);
        if (Objects.isNull(item)) {
            if (player.isGM()) {
                final WorldObject obj = World.getInstance().findObject(this.objectId);
                if (GameUtils.isItem(obj)) {
                    AdminCommandHandler.getInstance().useAdminCommand(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.objectId), true);
                }
            }
            return;
        }
        if (item.isQuestItem()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_QUEST_ITEMS);
            return;
        }
        if (player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead()) {
            return;
        }
        if (player.isDead() || player.getInventory().isBlocked(item)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addItemName(item);
            player.sendPacket(sm);
            return;
        }
        if (!item.isEquipped() && !item.getTemplate().checkCondition(player, player, true)) {
            return;
        }
        this.itemId = item.getId();
        if (player.isFishing() && !Util.isBetween(this.itemId, 6535, 6540)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && player.getReputation() < 0) {
            final List<ItemSkillHolder> skills = item.getSkills(ItemSkillType.NORMAL);
            if (skills != null && skills.stream().anyMatch(holder -> holder.getSkill().hasAnyEffectType(EffectType.TELEPORT))) {
                return;
            }
        }
        final int reuseDelay = item.getReuseDelay();
        final int sharedReuseGroup = item.getSharedReuseGroup();
        if (reuseDelay > 0) {
            final long reuse = player.getItemRemainingReuseTime(item.getObjectId());
            if (reuse > 0L) {
                this.reuseData(player, item, reuse);
                this.sendSharedGroupUpdate(player, sharedReuseGroup, reuse, reuseDelay);
                return;
            }
            final long reuseOnGroup = player.getReuseDelayOnGroup(sharedReuseGroup);
            if (reuseOnGroup > 0L) {
                this.reuseData(player, item, reuseOnGroup);
                this.sendSharedGroupUpdate(player, sharedReuseGroup, reuseOnGroup, reuseDelay);
                return;
            }
        }
        player.onActionRequest();
        if (item.isEquipable()) {
            this.handleEquipable(player, item);
        }
        else {
            final EtcItem etcItem = item.getEtcItem();
            final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
            if (Objects.isNull(handler)) {
                if (Objects.nonNull(etcItem) && Util.isNotEmpty(etcItem.getHandlerName())) {
                    UseItem.LOGGER.warn("Unmanaged Item handler: {} for Item Id: {}!", (Object)etcItem.getHandlerName(), (Object)this.itemId);
                }
            }
            else if (handler.useItem(player, item, this.ctrlPressed) && reuseDelay > 0) {
                player.addTimeStampItem(item, reuseDelay);
                this.sendSharedGroupUpdate(player, sharedReuseGroup, reuseDelay, reuseDelay);
            }
        }
    }
    
    private void handleEquipable(final Player player, final Item item) {
        if (!this.checkCanUse(player, item)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            return;
        }
        if (player.isCastingNow()) {
            player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> player.useEquippableItem(item, true)));
        }
        else if (player.isAttackingNow()) {
            final Item usedItem;
            ThreadPool.schedule(() -> {
                usedItem = player.getInventory().getItemByObjectId(this.objectId);
                if (!Objects.isNull(usedItem)) {
                    player.useEquippableItem(usedItem, false);
                }
            }, player.getAttackEndTime() - TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()));
        }
        else {
            player.useEquippableItem(item, true);
        }
    }
    
    private boolean checkCanUse(final Player player, final Item item) {
        final BodyPart bodyPart = item.getBodyPart();
        return this.checkUnlockedSlot(player, item, bodyPart) && (!item.isHeroItem() || player.isHero() || player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS)) && !player.getInventory().isItemSlotBlocked(bodyPart);
    }
    
    private boolean checkUnlockedSlot(final Player player, final Item item, final BodyPart bodyPart) {
        switch (bodyPart) {
            case TWO_HAND:
            case LEFT_HAND:
            case RIGHT_HAND: {
                if (player.isMounted() || player.isDisarmed()) {
                    return false;
                }
                break;
            }
            case TALISMAN: {
                if (!item.isEquipped() && player.getInventory().getTalismanSlots() == 0) {
                    player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET)).addItemName(item));
                    return false;
                }
                break;
            }
            case BROOCH_JEWEL: {
                if (!item.isEquipped() && player.getInventory().getBroochJewelSlots() == 0) {
                    player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_EQUIP_S1_WITHOUT_EQUIPPING_A_BROOCH)).addItemName(item));
                    return false;
                }
                break;
            }
            case AGATHION: {
                if (!item.isEquipped() && player.getInventory().getAgathionSlots() == 0) {
                    return false;
                }
                break;
            }
            case ARTIFACT: {
                if (!item.isEquipped() && player.getInventory().getArtifactSlots() == 0) {
                    player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.NO_ARTIFACT_BOOK_EQUIPPED_YOU_CANNOT_EQUIP_S1)).addItemName(item));
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    private void reuseData(final Player activeChar, final Item item, final long remainingTime) {
        final int hours = (int)(remainingTime / 3600000L);
        final int minutes = (int)(remainingTime % 3600000L) / 60000;
        final int seconds = (int)(remainingTime / 1000L % 60L);
        SystemMessage sm;
        if (hours > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
            sm.addInt(hours);
            sm.addInt(minutes);
        }
        else if (minutes > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
            sm.addInt(minutes);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
        }
        sm.addInt(seconds);
        activeChar.sendPacket(sm);
    }
    
    private void sendSharedGroupUpdate(final Player activeChar, final int group, final long remaining, final int reuse) {
        if (group > 0) {
            activeChar.sendPacket(new ExUseSharedGroupItem(this.itemId, group, remaining, reuse));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)UseItem.class);
    }
}
