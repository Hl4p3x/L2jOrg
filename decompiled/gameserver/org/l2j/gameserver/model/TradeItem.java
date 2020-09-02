// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.Collections;
import org.l2j.gameserver.enums.AttributeType;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import java.util.Collection;
import org.l2j.gameserver.model.item.ItemTemplate;

public class TradeItem
{
    private final int[] attributeDefense;
    private final ItemTemplate template;
    private int objectId;
    private long count;
    private int enchant;
    private VariationInstance augmentation;
    private final int locationSlot;
    private final int type1;
    private final int type2;
    private Collection<EnsoulOption> soulCrystalOptions;
    private Collection<EnsoulOption> soulCrystalSpecialOptions;
    private final int[] enchantOptions;
    private byte elemAtkType;
    private int elemAtkPower;
    private long _price;
    private long _storeCount;
    private int _augmentationOption1;
    private int _augmentationOption2;
    
    public TradeItem(final Item item, final long count, final long price) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this._augmentationOption1 = -1;
        this._augmentationOption2 = -1;
        Objects.requireNonNull(item);
        this.objectId = item.getObjectId();
        this.template = item.getTemplate();
        this.locationSlot = item.getLocationSlot();
        this.enchant = item.getEnchantLevel();
        this.type1 = item.getCustomType1();
        this.type2 = item.getType2();
        this.count = count;
        this._price = price;
        this.elemAtkType = item.getAttackAttributeType().getClientId();
        this.elemAtkPower = item.getAttackAttributePower();
        for (final AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            this.attributeDefense[type.getClientId()] = item.getDefenceAttribute(type);
        }
        this.enchantOptions = item.getEnchantOptions();
        this.soulCrystalOptions = item.getSpecialAbilities();
        this.soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
        this.augmentation = item.getAugmentation();
        if (item.getAugmentation() != null) {
            this._augmentationOption1 = item.getAugmentation().getOption1Id();
            this._augmentationOption1 = item.getAugmentation().getOption2Id();
        }
    }
    
    public TradeItem(final ItemTemplate item, final long count, final long price) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this._augmentationOption1 = -1;
        this._augmentationOption2 = -1;
        Objects.requireNonNull(item);
        this.objectId = 0;
        this.template = item;
        this.locationSlot = 0;
        this.enchant = 0;
        this.type1 = 0;
        this.type2 = 0;
        this.count = count;
        this._storeCount = count;
        this._price = price;
        this.elemAtkType = AttributeType.NONE.getClientId();
        this.elemAtkPower = 0;
        this.enchantOptions = Item.DEFAULT_ENCHANT_OPTIONS;
        this.soulCrystalOptions = (Collection<EnsoulOption>)Collections.emptyList();
        this.soulCrystalSpecialOptions = (Collection<EnsoulOption>)Collections.emptyList();
    }
    
    public TradeItem(final TradeItem item, final long count, final long price) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this._augmentationOption1 = -1;
        this._augmentationOption2 = -1;
        Objects.requireNonNull(item);
        this.objectId = item.getObjectId();
        this.template = item.getItem();
        this.locationSlot = item.getLocationSlot();
        this.enchant = item.getEnchant();
        this.type1 = item.getCustomType1();
        this.type2 = item.getCustomType2();
        this.count = count;
        this._storeCount = count;
        this._price = price;
        this.elemAtkType = item.getAttackElementType();
        this.elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; ++i) {
            this.attributeDefense[i] = item.getElementDefAttr(i);
        }
        this.enchantOptions = item.getEnchantOptions();
        this.soulCrystalOptions = item.getSoulCrystalOptions();
        this.soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
    }
    
    public int getObjectId() {
        return this.objectId;
    }
    
    public void setObjectId(final int objectId) {
        this.objectId = objectId;
    }
    
    public ItemTemplate getItem() {
        return this.template;
    }
    
    public int getLocationSlot() {
        return this.locationSlot;
    }
    
    public int getEnchant() {
        return this.enchant;
    }
    
    public void setEnchant(final int enchant) {
        this.enchant = enchant;
    }
    
    public int getCustomType1() {
        return this.type1;
    }
    
    public int getCustomType2() {
        return this.type2;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public void setCount(final long count) {
        this.count = count;
    }
    
    public long getStoreCount() {
        return this._storeCount;
    }
    
    public long getPrice() {
        return this._price;
    }
    
    public void setPrice(final long price) {
        this._price = price;
    }
    
    public byte getAttackElementType() {
        return this.elemAtkType;
    }
    
    public void setAttackElementType(final AttributeType attackElement) {
        this.elemAtkType = attackElement.getClientId();
    }
    
    public int getAttackElementPower() {
        return this.elemAtkPower;
    }
    
    public void setAttackElementPower(final int attackElementPower) {
        this.elemAtkPower = attackElementPower;
    }
    
    public void setElementDefAttr(final AttributeType element, final int value) {
        this.attributeDefense[element.getClientId()] = value;
    }
    
    public int getElementDefAttr(final byte i) {
        return this.attributeDefense[i];
    }
    
    public int[] getEnchantOptions() {
        return this.enchantOptions;
    }
    
    public Collection<EnsoulOption> getSoulCrystalOptions() {
        return (Collection<EnsoulOption>)((this.soulCrystalOptions == null) ? Collections.emptyList() : this.soulCrystalOptions);
    }
    
    public void setSoulCrystalOptions(final Collection<EnsoulOption> soulCrystalOptions) {
        this.soulCrystalOptions = soulCrystalOptions;
    }
    
    public Collection<EnsoulOption> getSoulCrystalSpecialOptions() {
        return (Collection<EnsoulOption>)((this.soulCrystalSpecialOptions == null) ? Collections.emptyList() : this.soulCrystalSpecialOptions);
    }
    
    public void setSoulCrystalSpecialOptions(final Collection<EnsoulOption> soulCrystalSpecialOptions) {
        this.soulCrystalSpecialOptions = soulCrystalSpecialOptions;
    }
    
    public void setAugmentation(final int option1, final int option2) {
        this._augmentationOption1 = option1;
        this._augmentationOption2 = option2;
    }
    
    public int getAugmentationOption1() {
        return this._augmentationOption1;
    }
    
    public int getAugmentationOption2() {
        return this._augmentationOption2;
    }
}
