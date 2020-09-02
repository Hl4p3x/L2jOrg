// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.container.PlayerInventory;
import java.util.Iterator;
import org.l2j.gameserver.model.buylist.Product;
import java.util.Objects;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.enums.ItemListType;

public abstract class AbstractItemPacket extends AbstractMaskPacket<ItemListType>
{
    private static final byte[] MASKS;
    
    protected static int calculateMask(final ItemInfo item) {
        int mask = 0;
        if (item.getAugmentation() != null) {
            mask |= ItemListType.AUGMENT_BONUS.getMask();
        }
        if (item.getAttackElementType() >= 0 || item.getAttributeDefence(AttributeType.FIRE) > 0 || item.getAttributeDefence(AttributeType.WATER) > 0 || item.getAttributeDefence(AttributeType.WIND) > 0 || item.getAttributeDefence(AttributeType.EARTH) > 0 || item.getAttributeDefence(AttributeType.HOLY) > 0 || item.getAttributeDefence(AttributeType.DARK) > 0) {
            mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
        }
        if (item.getEnchantOptions() != null) {
            for (final int id : item.getEnchantOptions()) {
                if (id > 0) {
                    mask |= ItemListType.ENCHANT_EFFECT.getMask();
                    break;
                }
            }
        }
        if ((item.getSoulCrystalOptions() != null && !item.getSoulCrystalOptions().isEmpty()) || (item.getSoulCrystalSpecialOptions() != null && !item.getSoulCrystalSpecialOptions().isEmpty())) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
        }
        if (item.getReuse() > 0) {
            mask |= ItemListType.REUSE_DELAY.getMask();
        }
        return mask;
    }
    
    @Override
    protected byte[] getMasks() {
        return AbstractItemPacket.MASKS;
    }
    
    protected void writeItem(final TradeItem item, final long count) {
        this.writeItem(new ItemInfo(item), count);
    }
    
    protected void writeItem(final TradeItem item) {
        this.writeItem(new ItemInfo(item));
    }
    
    protected void writeItem(final Item item, final Player owner) {
        final int mask = this.calculateMask(item);
        this.writeByte(mask);
        this.writeInt(item.getObjectId());
        this.writeInt(item.getDisplayId());
        this.writeByte((item.isQuestItem() || item.isEquipped()) ? 255 : item.getLocationSlot());
        this.writeLong(item.getCount());
        this.writeByte(item.getType2());
        this.writeByte(0);
        this.writeShort(item.isEquipped());
        this.writeLong(item.getBodyPart().getId());
        this.writeByte(item.getEnchantLevel());
        this.writeByte(0);
        this.writeByte(0);
        this.writeInt(-1);
        this.writeInt(item.isTimeLimitedItem() ? ((int)(item.getRemainingTime() / 1000L)) : -9999);
        this.writeByte(item.isAvailable());
        this.writeShort(0);
        if (this.containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            this.writeItemAugment(item);
        }
        if (this.containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            this.writeItemElemental(item);
        }
        if (this.containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            this.writeItemEnchantEffect(item);
        }
        if (this.containsMask(mask, ItemListType.VISUAL_ID)) {
            this.writeInt(item.getDisplayId());
        }
        if (this.containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            this.writeSoulCrystalInfo(item);
        }
        if (this.containsMask(mask, ItemListType.REUSE_DELAY)) {
            this.writeInt((int)owner.getItemRemainingReuseTime(item.getObjectId()) / 1000);
        }
    }
    
    private void writeSoulCrystalInfo(final Item item) {
        this.writeByte(item.getSpecialAbilities().size());
        item.getSpecialAbilities().stream().mapToInt(EnsoulOption::getId).forEach(x$0 -> this.writeInt(x$0));
        this.writeByte(item.getAdditionalSpecialAbilities().size());
        item.getAdditionalSpecialAbilities().stream().mapToInt(EnsoulOption::getId).forEach(x$0 -> this.writeInt(x$0));
    }
    
    private void writeItemEnchantEffect(final Item item) {
        for (final int op : item.getEnchantOptions()) {
            this.writeInt(op);
        }
    }
    
    private void writeItemElemental(final Item item) {
        this.writeShort((short)item.getAttackAttributeType().getClientId());
        this.writeShort(item.getAttackAttributePower());
        for (final AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            this.writeShort(item.getDefenceAttribute(type));
        }
    }
    
    private void writeItemAugment(final Item item) {
        this.writeInt(Util.zeroIfNullOrElse((Object)item.getAugmentation(), VariationInstance::getOption1Id));
        this.writeInt(Util.zeroIfNullOrElse((Object)item.getAugmentation(), VariationInstance::getOption2Id));
    }
    
    private int calculateMask(final Item item) {
        int mask = 0;
        if (Objects.nonNull(item.getAugmentation())) {
            mask |= ItemListType.AUGMENT_BONUS.getMask();
        }
        if (this.hasAttribute(item)) {
            mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
        }
        if (Objects.nonNull(item.getEnchantOptions())) {
            for (final int id : item.getEnchantOptions()) {
                if (id > 0) {
                    mask |= ItemListType.ENCHANT_EFFECT.getMask();
                    break;
                }
            }
        }
        if (!item.getSpecialAbilities().isEmpty() || !item.getAdditionalSpecialAbilities().isEmpty()) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
        }
        if (item.getReuseDelay() > 0) {
            mask |= ItemListType.REUSE_DELAY.getMask();
        }
        return mask;
    }
    
    private boolean hasAttribute(final Item item) {
        return item.getAttackAttributeType() != AttributeType.NONE || item.getDefenceAttribute(AttributeType.FIRE) > 0 || item.getDefenceAttribute(AttributeType.WATER) > 0 || item.getDefenceAttribute(AttributeType.WIND) > 0 || item.getDefenceAttribute(AttributeType.EARTH) > 0 || item.getDefenceAttribute(AttributeType.HOLY) > 0 || item.getDefenceAttribute(AttributeType.DARK) > 0;
    }
    
    protected void writeItem(final Item item) {
        this.writeItem(new ItemInfo(item));
    }
    
    protected void writeItem(final Product item) {
        this.writeItem(new ItemInfo(item));
    }
    
    protected void writeItem(final ItemInfo item) {
        this.writeItem(item, item.getCount());
    }
    
    protected void writeItem(final ItemInfo item, final long count) {
        final int mask = calculateMask(item);
        this.writeByte(mask);
        this.writeInt(item.getObjectId());
        this.writeInt(item.getDisplayId());
        this.writeByte((item.isQuestItem() || item.getEquipped() == 1) ? 255 : item.getLocationSlot());
        this.writeLong(count);
        this.writeByte(item.getType2());
        this.writeByte(0);
        this.writeShort(item.getEquipped());
        this.writeLong(item.getBodyPart().getId());
        this.writeShort(item.getEnchantLevel());
        this.writeByte(0);
        this.writeInt(-1);
        this.writeInt(item.getTime());
        this.writeByte(item.isAvailable());
        this.writeShort(0);
        if (this.containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            this.writeItemAugment(item);
        }
        if (this.containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            this.writeItemElemental(item);
        }
        if (this.containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            this.writeItemEnchantEffect(item);
        }
        if (this.containsMask(mask, ItemListType.VISUAL_ID)) {
            this.writeInt(item.getDisplayId());
        }
        if (this.containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            this.writeItemEnsoulOptions(item);
        }
        if (this.containsMask(mask, ItemListType.REUSE_DELAY)) {
            this.writeInt(item.getReuse());
        }
    }
    
    private void writeSoulCrystalInfo(final ItemInfo item) {
        this.writeByte(item.getSoulCrystalOptions().size());
        for (final EnsoulOption option : item.getSoulCrystalOptions()) {
            this.writeInt(option.getId());
        }
        this.writeByte(item.getSoulCrystalSpecialOptions().size());
        for (final EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
            this.writeInt(option.getId());
        }
    }
    
    protected void writeItemAugment(final ItemInfo item) {
        if (item != null && item.getAugmentation() != null) {
            this.writeInt(item.getAugmentation().getOption1Id());
            this.writeInt(item.getAugmentation().getOption2Id());
        }
        else {
            this.writeInt(0);
            this.writeInt(0);
        }
    }
    
    protected void writeItemElemental(final ItemInfo item) {
        if (item != null) {
            this.writeShort(item.getAttackElementType());
            this.writeShort(item.getAttackElementPower());
            this.writeShort(item.getAttributeDefence(AttributeType.FIRE));
            this.writeShort(item.getAttributeDefence(AttributeType.WATER));
            this.writeShort(item.getAttributeDefence(AttributeType.WIND));
            this.writeShort(item.getAttributeDefence(AttributeType.EARTH));
            this.writeShort(item.getAttributeDefence(AttributeType.HOLY));
            this.writeShort(item.getAttributeDefence(AttributeType.DARK));
        }
        else {
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
        }
    }
    
    protected void writeItemEnchantEffect(final ItemInfo item) {
        for (final int op : item.getEnchantOptions()) {
            this.writeInt(op);
        }
    }
    
    protected void writeItemEnsoulOptions(final ItemInfo item) {
        if (item != null) {
            this.writeSoulCrystalInfo(item);
        }
        else {
            this.writeByte(0);
            this.writeByte(0);
        }
    }
    
    protected void writeInventoryBlock(final PlayerInventory inventory) {
        if (inventory.hasInventoryBlock()) {
            this.writeShort(inventory.getBlockItems().size());
            this.writeByte(inventory.getBlockMode().getClientId());
            inventory.getBlockItems().forEach(x$0 -> this.writeInt(x$0));
        }
        else {
            this.writeShort(0);
        }
    }
    
    static {
        MASKS = new byte[] { 0 };
    }
}
