// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.item.BodyPart;
import java.util.Collections;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.enums.AttributeType;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import java.util.Collection;
import org.l2j.gameserver.model.item.ItemTemplate;

public class ItemInfo
{
    private final int[] attributeDefense;
    private ItemTemplate template;
    private int objectId;
    private long count;
    private int enchant;
    private VariationInstance augmentation;
    private int locationSlot;
    private int type1;
    private int type2;
    private Collection<EnsoulOption> soulCrystalOptions;
    private Collection<EnsoulOption> soulCrystalSpecialOptions;
    private int[] enchantOption;
    private byte elemAtkType;
    private int elemAtkPower;
    private int time;
    private int price;
    private int _change;
    private boolean _available;
    private int _equipped;
    private int reuse;
    
    public ItemInfo(final Item item) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this.elemAtkType = -2;
        this.elemAtkPower = 0;
        this._available = true;
        Objects.requireNonNull(item);
        this.objectId = item.getObjectId();
        this.template = item.getTemplate();
        this.enchant = item.getEnchantLevel();
        this.augmentation = item.getAugmentation();
        this.count = item.getCount();
        this.type1 = item.getCustomType1();
        this.type2 = item.getType2();
        this._equipped = (item.isEquipped() ? 1 : 0);
        switch (item.getLastChange()) {
            case 1: {
                this._change = 1;
                break;
            }
            case 2: {
                this._change = 2;
                break;
            }
            case 3: {
                this._change = 3;
                break;
            }
        }
        this.time = (item.isTimeLimitedItem() ? ((int)(item.getRemainingTime() / 1000L)) : -9999);
        this._available = item.isAvailable();
        this.locationSlot = item.getLocationSlot();
        this.elemAtkType = item.getAttackAttributeType().getClientId();
        this.elemAtkPower = item.getAttackAttributePower();
        for (final AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            this.attributeDefense[type.getClientId()] = item.getDefenceAttribute(type);
        }
        this.enchantOption = item.getEnchantOptions();
        this.soulCrystalOptions = item.getSpecialAbilities();
        this.soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
    }
    
    public ItemInfo(final Item item, final int change) {
        this(item);
        this._change = change;
    }
    
    public ItemInfo(final TradeItem item) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this.elemAtkType = -2;
        this.elemAtkPower = 0;
        this._available = true;
        if (item == null) {
            return;
        }
        this.objectId = item.getObjectId();
        this.template = item.getItem();
        this.enchant = item.getEnchant();
        if (item.getAugmentationOption1() >= 0 && item.getAugmentationOption2() >= 0) {
            this.augmentation = new VariationInstance(0, item.getAugmentationOption1(), item.getAugmentationOption2());
        }
        this.count = item.getCount();
        this.type1 = item.getCustomType1();
        this.type2 = item.getCustomType2();
        this._equipped = 0;
        this._change = 0;
        this.time = -9999;
        this.locationSlot = item.getLocationSlot();
        this.elemAtkType = item.getAttackElementType();
        this.elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; ++i) {
            this.attributeDefense[i] = item.getElementDefAttr(i);
        }
        this.enchantOption = item.getEnchantOptions();
        this.soulCrystalOptions = item.getSoulCrystalOptions();
        this.soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
    }
    
    public ItemInfo(final Product item) {
        this.attributeDefense = new int[] { 0, 0, 0, 0, 0, 0 };
        this.elemAtkType = -2;
        this.elemAtkPower = 0;
        this._available = true;
        if (item == null) {
            return;
        }
        this.objectId = 0;
        this.template = item.getTemplate();
        this.enchant = 0;
        this.augmentation = null;
        this.count = item.getCount();
        this.type1 = this.template.getType1();
        this.type2 = this.template.getType2();
        this._equipped = 0;
        this._change = 0;
        this.time = -9999;
        this.locationSlot = 0;
        this.soulCrystalOptions = (Collection<EnsoulOption>)Collections.emptyList();
        this.soulCrystalSpecialOptions = (Collection<EnsoulOption>)Collections.emptyList();
    }
    
    public int getObjectId() {
        return this.objectId;
    }
    
    public ItemTemplate getTemplate() {
        return this.template;
    }
    
    public int getEnchantLevel() {
        return this.enchant;
    }
    
    public VariationInstance getAugmentation() {
        return this.augmentation;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public int getPrice() {
        return this.price;
    }
    
    public int getCustomType1() {
        return this.type1;
    }
    
    public int getEquipped() {
        return this._equipped;
    }
    
    public int getChange() {
        return this._change;
    }
    
    public int getTime() {
        return (this.time > 0) ? this.time : -9999;
    }
    
    public boolean isAvailable() {
        return this._available;
    }
    
    public int getLocationSlot() {
        return this.locationSlot;
    }
    
    public int getAttackElementType() {
        return this.elemAtkType;
    }
    
    public int getAttackElementPower() {
        return this.elemAtkPower;
    }
    
    public int getAttributeDefence(final AttributeType attribute) {
        return this.attributeDefense[attribute.getClientId()];
    }
    
    public int[] getEnchantOptions() {
        return this.enchantOption;
    }
    
    public Collection<EnsoulOption> getSoulCrystalOptions() {
        return (Collection<EnsoulOption>)((this.soulCrystalOptions != null) ? this.soulCrystalOptions : Collections.emptyList());
    }
    
    public Collection<EnsoulOption> getSoulCrystalSpecialOptions() {
        return (Collection<EnsoulOption>)((this.soulCrystalSpecialOptions != null) ? this.soulCrystalSpecialOptions : Collections.emptyList());
    }
    
    public int getId() {
        return this.template.getId();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/ItemTemplate;IJ)Ljava/lang/String;, this.template, this.objectId, this.count);
    }
    
    public BodyPart getBodyPart() {
        return this.template.getBodyPart();
    }
    
    public int getType2() {
        return this.template.getType2();
    }
    
    public int getDisplayId() {
        return this.template.getDisplayId();
    }
    
    public boolean isQuestItem() {
        return this.template.isQuestItem();
    }
    
    public void setReuse(final int reuse) {
        this.reuse = reuse;
    }
    
    public int getReuse() {
        return this.reuse;
    }
}
