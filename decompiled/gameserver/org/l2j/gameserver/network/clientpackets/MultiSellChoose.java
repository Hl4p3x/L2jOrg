// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import java.util.OptionalLong;
import java.util.List;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import java.util.Collections;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.holders.MultisellEntryHolder;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.slf4j.Logger;

public class MultiSellChoose extends ClientPacket
{
    private static final Logger LOGGER;
    private int _listId;
    private int _entryId;
    private long _amount;
    private int _enchantLevel;
    private int _augmentOption1;
    private int _augmentOption2;
    private short _attackAttribute;
    private short _attributePower;
    private short _fireDefence;
    private short _waterDefence;
    private short _windDefence;
    private short _earthDefence;
    private short _holyDefence;
    private short _darkDefence;
    private EnsoulOption[] _soulCrystalOptions;
    private EnsoulOption[] _soulCrystalSpecialOptions;
    
    public void readImpl() {
        this._listId = this.readInt();
        this._entryId = this.readInt();
        this._amount = this.readLong();
        this._enchantLevel = this.readShort();
        this._augmentOption1 = this.readInt();
        this._augmentOption2 = this.readInt();
        this._attackAttribute = this.readShort();
        this._attributePower = this.readShort();
        this._fireDefence = this.readShort();
        this._waterDefence = this.readShort();
        this._windDefence = this.readShort();
        this._earthDefence = this.readShort();
        this._holyDefence = this.readShort();
        this._darkDefence = this.readShort();
        this._soulCrystalOptions = new EnsoulOption[this.readByte()];
        for (int i = 0; i < this._soulCrystalOptions.length; ++i) {
            final int ensoulId = this.readInt();
            this._soulCrystalOptions[i] = EnsoulData.getInstance().getOption(ensoulId);
        }
        this._soulCrystalSpecialOptions = new EnsoulOption[this.readByte()];
        for (int i = 0; i < this._soulCrystalSpecialOptions.length; ++i) {
            final int ensoulId = this.readInt();
            this._soulCrystalSpecialOptions[i] = EnsoulData.getInstance().getOption(ensoulId);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getMultiSell().tryPerformAction("multisell choose")) {
            player.setMultiSell(null);
            return;
        }
        if (this._amount < 1L || this._amount > 10000L) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        final PreparedMultisellListHolder list = player.getMultiSell();
        if (list == null || list.getId() != this._listId) {
            player.setMultiSell(null);
            return;
        }
        final Npc npc = player.getLastFolkNPC();
        if (!list.isNpcAllowed(-1) && (Objects.isNull(npc) || !list.isNpcAllowed(npc.getId()))) {
            if (!player.isGM()) {
                player.setMultiSell(null);
                return;
            }
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._listId));
        }
        if (!player.isGM() && Objects.nonNull(npc) && (!MathUtil.isInsideRadius3D(player, npc, 250) || player.getInstanceId() != npc.getInstanceId())) {
            player.setMultiSell(null);
            return;
        }
        if ((this._soulCrystalOptions != null && CommonUtil.contains((Object[])this._soulCrystalOptions, (Object)null)) || (this._soulCrystalSpecialOptions != null && CommonUtil.contains((Object[])this._soulCrystalSpecialOptions, (Object)null))) {
            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, this._entryId));
            player.setMultiSell(null);
            return;
        }
        final MultisellEntryHolder entry = list.getEntries().get(this._entryId - 1);
        if (entry == null) {
            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, this._entryId));
            player.setMultiSell(null);
            return;
        }
        if (!entry.isStackable() && this._amount > 1L) {
            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, this._entryId));
            player.setMultiSell(null);
            return;
        }
        final ItemInfo itemEnchantment = list.getItemEnchantment(this._entryId - 1);
        if (itemEnchantment != null && (this._amount > 1L || itemEnchantment.getEnchantLevel() != this._enchantLevel || itemEnchantment.getAttackElementType() != this._attackAttribute || itemEnchantment.getAttackElementPower() != this._attributePower || itemEnchantment.getAttributeDefence(AttributeType.FIRE) != this._fireDefence || itemEnchantment.getAttributeDefence(AttributeType.WATER) != this._waterDefence || itemEnchantment.getAttributeDefence(AttributeType.WIND) != this._windDefence || itemEnchantment.getAttributeDefence(AttributeType.EARTH) != this._earthDefence || itemEnchantment.getAttributeDefence(AttributeType.HOLY) != this._holyDefence || itemEnchantment.getAttributeDefence(AttributeType.DARK) != this._darkDefence || (itemEnchantment.getAugmentation() == null && (this._augmentOption1 != 0 || this._augmentOption2 != 0)) || (itemEnchantment.getAugmentation() != null && (itemEnchantment.getAugmentation().getOption1Id() != this._augmentOption1 || itemEnchantment.getAugmentation().getOption2Id() != this._augmentOption2)) || (this._soulCrystalOptions != null && itemEnchantment.getSoulCrystalOptions().stream().anyMatch(e -> !CommonUtil.contains((Object[])this._soulCrystalOptions, (Object)e))) || (this._soulCrystalOptions == null && !itemEnchantment.getSoulCrystalOptions().isEmpty()) || (this._soulCrystalSpecialOptions != null && itemEnchantment.getSoulCrystalSpecialOptions().stream().anyMatch(e -> !CommonUtil.contains((Object[])this._soulCrystalSpecialOptions, (Object)e))) || (this._soulCrystalSpecialOptions == null && !itemEnchantment.getSoulCrystalSpecialOptions().isEmpty()))) {
            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, this._entryId));
            player.setMultiSell(null);
            return;
        }
        final Clan clan = player.getClan();
        final PlayerInventory inventory = player.getInventory();
        try {
            int slots = 0;
            int weight = 0;
            for (final ItemChanceHolder product : entry.getProducts()) {
                if (product.getId() < 0) {
                    if (clan == null && SpecialItemType.CLAN_REPUTATION.getClientId() == product.getId()) {
                        player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
                        return;
                    }
                    continue;
                }
                else {
                    final ItemTemplate template = ItemEngine.getInstance().getTemplate(product.getId());
                    if (template == null) {
                        player.setMultiSell(null);
                        return;
                    }
                    final long totalCount = Math.multiplyExact(list.getProductCount(product), this._amount);
                    if (totalCount <= 0L || totalCount > 2147483647L) {
                        player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                        return;
                    }
                    if (!template.isStackable() || player.getInventory().getItemByItemId(product.getId()) == null) {
                        ++slots;
                    }
                    weight += (int)(totalCount * template.getWeight());
                    if (!inventory.validateWeight(weight)) {
                        player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        return;
                    }
                    if (slots > 0 && !inventory.validateCapacity(slots)) {
                        player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
                        return;
                    }
                    if (!list.isChanceMultisell()) {
                        continue;
                    }
                    slots = 0;
                    weight = 0;
                }
            }
            if (itemEnchantment != null && inventory.getItemByObjectId(itemEnchantment.getObjectId()) == null) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
                sm.addItemName(itemEnchantment.getId());
                player.sendPacket(sm);
                return;
            }
            for (final ItemChanceHolder ingredient : entry.getIngredients()) {
                if (ingredient.getEnchantmentLevel() > 0) {
                    int found = 0;
                    for (final Item item : inventory.getAllItemsByItemId(ingredient.getId(), ingredient.getEnchantmentLevel())) {
                        if (item.getEnchantLevel() >= ingredient.getEnchantmentLevel()) {
                            ++found;
                        }
                    }
                    if (found < ingredient.getCount()) {
                        final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
                        sm2.addString(invokedynamic(makeConcatWithConstants:(BLjava/lang/String;)Ljava/lang/String;, ingredient.getEnchantmentLevel(), ItemEngine.getInstance().getTemplate(ingredient.getId()).getName()));
                        player.sendPacket(sm2);
                        return;
                    }
                    continue;
                }
                else {
                    if (!this.checkIngredients(player, list, inventory, clan, ingredient.getId(), Math.multiplyExact(ingredient.getCount(), this._amount))) {
                        return;
                    }
                    continue;
                }
            }
            final InventoryUpdate iu = new InventoryUpdate();
            boolean itemEnchantmentProcessed = itemEnchantment == null;
            for (final ItemChanceHolder ingredient2 : entry.getIngredients()) {
                if (ingredient2.isMaintainIngredient()) {
                    continue;
                }
                final long totalCount2 = Math.multiplyExact(list.getIngredientCount(ingredient2), this._amount);
                final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredient2.getId());
                if (specialItem != null) {
                    switch (specialItem) {
                        case CLAN_REPUTATION: {
                            if (clan != null) {
                                clan.takeReputationScore((int)totalCount2, true);
                                final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                                smsg.addLong(totalCount2);
                                player.sendPacket(smsg);
                                continue;
                            }
                            continue;
                        }
                        case FAME: {
                            player.setFame(player.getFame() - (int)totalCount2);
                            player.sendPacket(new UserInfo(player));
                            continue;
                        }
                        case RAIDBOSS_POINTS: {
                            player.setRaidbossPoints(player.getRaidbossPoints() - (int)totalCount2);
                            player.sendPacket(new UserInfo(player));
                            player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_CONSUMED_S1_RAID_POINTS)).addLong(totalCount2));
                            continue;
                        }
                        case PC_CAFE_POINTS: {
                            player.setPcCafePoints((int)(player.getPcCafePoints() - totalCount2));
                            player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), (int)(-totalCount2), 1));
                            continue;
                        }
                        default: {
                            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, ingredient2.getId()));
                            return;
                        }
                    }
                }
                else if (ingredient2.getEnchantmentLevel() > 0) {
                    final Item destroyedItem = inventory.destroyItem("Multisell", inventory.getAllItemsByItemId(ingredient2.getId(), ingredient2.getEnchantmentLevel()).iterator().next(), totalCount2, player, npc);
                    if (destroyedItem == null) {
                        final SystemMessage sm3 = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
                        sm3.addItemName(ingredient2.getId());
                        player.sendPacket(sm3);
                        return;
                    }
                    itemEnchantmentProcessed = true;
                    iu.addItem(destroyedItem);
                }
                else if (!itemEnchantmentProcessed && itemEnchantment != null && itemEnchantment.getId() == ingredient2.getId()) {
                    final Item destroyedItem = inventory.destroyItem("Multisell", itemEnchantment.getObjectId(), totalCount2, player, npc);
                    if (destroyedItem == null) {
                        final SystemMessage sm3 = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
                        sm3.addItemName(ingredient2.getId());
                        player.sendPacket(sm3);
                        return;
                    }
                    itemEnchantmentProcessed = true;
                    iu.addItem(destroyedItem);
                }
                else {
                    final Item destroyedItem = inventory.destroyItemByItemId("Multisell", ingredient2.getId(), totalCount2, player, npc);
                    if (destroyedItem == null) {
                        final SystemMessage sm3 = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_S2_S1_S);
                        sm3.addItemName(ingredient2.getId());
                        sm3.addLong(totalCount2);
                        player.sendPacket(sm3);
                        return;
                    }
                    iu.addItem(destroyedItem);
                }
            }
            List<ItemChanceHolder> products = entry.getProducts();
            if (list.isChanceMultisell()) {
                final ItemChanceHolder randomProduct = ItemChanceHolder.getRandomHolder(entry.getProducts());
                products = ((randomProduct != null) ? Collections.singletonList(randomProduct) : Collections.emptyList());
            }
            for (final ItemChanceHolder product2 : products) {
                final long totalCount3 = Math.multiplyExact(list.getProductCount(product2), this._amount);
                final SpecialItemType specialItem2 = SpecialItemType.getByClientId(product2.getId());
                if (specialItem2 != null) {
                    switch (specialItem2) {
                        case CLAN_REPUTATION: {
                            if (clan != null) {
                                clan.addReputationScore((int)totalCount3, true);
                                continue;
                            }
                            continue;
                        }
                        case FAME: {
                            player.setFame((int)(player.getFame() + totalCount3));
                            player.sendPacket(new UserInfo(player));
                            continue;
                        }
                        case RAIDBOSS_POINTS: {
                            player.increaseRaidbossPoints((int)totalCount3);
                            player.sendPacket(new UserInfo(player));
                            continue;
                        }
                        default: {
                            MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this._listId, product2.getId()));
                            return;
                        }
                    }
                }
                else {
                    final Item addedItem = inventory.addItem("Multisell", product2.getId(), totalCount3, player, npc, false);
                    if (itemEnchantmentProcessed && list.isMaintainEnchantment() && itemEnchantment != null && addedItem.isEquipable() && addedItem.getTemplate().getClass().equals(itemEnchantment.getTemplate().getClass())) {
                        addedItem.setEnchantLevel(itemEnchantment.getEnchantLevel());
                        addedItem.setAugmentation(itemEnchantment.getAugmentation(), false);
                        if (addedItem.isWeapon()) {
                            if (itemEnchantment.getAttackElementPower() > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.findByClientId(itemEnchantment.getAttackElementType()), itemEnchantment.getAttackElementPower()), false);
                            }
                        }
                        else {
                            if (itemEnchantment.getAttributeDefence(AttributeType.FIRE) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.FIRE, itemEnchantment.getAttributeDefence(AttributeType.FIRE)), false);
                            }
                            if (itemEnchantment.getAttributeDefence(AttributeType.WATER) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.WATER, itemEnchantment.getAttributeDefence(AttributeType.WATER)), false);
                            }
                            if (itemEnchantment.getAttributeDefence(AttributeType.WIND) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.WIND, itemEnchantment.getAttributeDefence(AttributeType.WIND)), false);
                            }
                            if (itemEnchantment.getAttributeDefence(AttributeType.EARTH) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.EARTH, itemEnchantment.getAttributeDefence(AttributeType.EARTH)), false);
                            }
                            if (itemEnchantment.getAttributeDefence(AttributeType.HOLY) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.HOLY, itemEnchantment.getAttributeDefence(AttributeType.HOLY)), false);
                            }
                            if (itemEnchantment.getAttributeDefence(AttributeType.DARK) > 0) {
                                addedItem.setAttribute(new AttributeHolder(AttributeType.DARK, itemEnchantment.getAttributeDefence(AttributeType.DARK)), false);
                            }
                        }
                        if (this._soulCrystalOptions != null) {
                            int pos = -1;
                            for (final EnsoulOption ensoul : this._soulCrystalOptions) {
                                ++pos;
                                addedItem.addSpecialAbility(ensoul, pos, 1, false);
                            }
                        }
                        if (this._soulCrystalSpecialOptions != null) {
                            for (final EnsoulOption ensoul2 : this._soulCrystalSpecialOptions) {
                                addedItem.addSpecialAbility(ensoul2, 0, 2, false);
                            }
                        }
                        addedItem.updateDatabase(true);
                        itemEnchantmentProcessed = false;
                    }
                    if (product2.getEnchantmentLevel() > 0) {
                        addedItem.setEnchantLevel(product2.getEnchantmentLevel());
                        addedItem.updateDatabase(true);
                    }
                    if (addedItem.getCount() > 1L) {
                        final SystemMessage sm4 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                        sm4.addItemName(addedItem.getId());
                        sm4.addLong(totalCount3);
                        player.sendPacket(sm4);
                    }
                    else if (addedItem.getEnchantLevel() > 0) {
                        final SystemMessage sm4 = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_S2);
                        sm4.addLong(addedItem.getEnchantLevel());
                        sm4.addItemName(addedItem.getId());
                        player.sendPacket(sm4);
                    }
                    else {
                        final SystemMessage sm4 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                        sm4.addItemName(addedItem);
                        player.sendPacket(sm4);
                    }
                    iu.addItem(addedItem);
                }
            }
            player.sendInventoryUpdate(iu);
            if (npc != null && list.isApplyTaxes()) {
                final PreparedMultisellListHolder preparedMultisellListHolder;
                final OptionalLong taxPaid = entry.getIngredients().stream().filter(i -> i.getId() == 57).mapToLong(i -> Math.round(i.getCount() * preparedMultisellListHolder.getIngredientMultiplier() * preparedMultisellListHolder.getTaxRate()) * this._amount).reduce(Math::multiplyExact);
                if (taxPaid.isPresent()) {
                    npc.handleTaxPayment(taxPaid.getAsLong());
                }
            }
        }
        catch (ArithmeticException ae) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        if (list.isInventoryOnly()) {
            MultisellData.getInstance().separateAndSend(list.getId(), player, npc, list.isInventoryOnly(), list.getProductMultiplier(), list.getIngredientMultiplier());
        }
    }
    
    private boolean checkIngredients(final Player player, final PreparedMultisellListHolder list, final PlayerInventory inventory, final Clan clan, final int ingredientId, final long totalCount) {
        final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredientId);
        if (specialItem != null) {
            switch (specialItem) {
                case CLAN_REPUTATION: {
                    if (clan == null) {
                        player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
                        return false;
                    }
                    if (!player.isClanLeader()) {
                        player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                        return false;
                    }
                    if (clan.getReputationScore() < totalCount) {
                        player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
                        return false;
                    }
                    return true;
                }
                case FAME: {
                    if (player.getFame() < totalCount) {
                        player.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_FAME_TO_DO_THAT);
                        return false;
                    }
                    return true;
                }
                case RAIDBOSS_POINTS: {
                    if (player.getRaidbossPoints() < totalCount) {
                        player.sendPacket(SystemMessageId.NOT_ENOUGH_RAID_POINTS);
                        return false;
                    }
                    return true;
                }
                case PC_CAFE_POINTS: {
                    if (player.getPcCafePoints() < totalCount) {
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_SHORT_OF_PA_POINTS));
                        return false;
                    }
                    return true;
                }
                default: {
                    MultiSellChoose.LOGGER.error(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this._listId, ingredientId));
                    return false;
                }
            }
        }
        else {
            if (inventory.getInventoryItemCount(ingredientId, list.isMaintainEnchantment() ? -1 : 0, false) < totalCount) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_S2_S1_S);
                sm.addItemName(ingredientId);
                sm.addLong(totalCount);
                player.sendPacket(sm);
                return false;
            }
            return true;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MultiSellChoose.class);
    }
}
