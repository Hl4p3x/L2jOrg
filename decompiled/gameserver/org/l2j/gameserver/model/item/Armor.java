// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.ArmorType;

public final class Armor extends ItemTemplate implements EquipableItem
{
    private ArmorType type;
    
    public Armor(final int id, final String name, final ArmorType type, final BodyPart bodyPart) {
        super(id, name);
        this.type = type;
        this.bodyPart = bodyPart;
        if (bodyPart.isAnyOf(BodyPart.NECK, BodyPart.EAR, BodyPart.FINGER, BodyPart.RIGHT_BRACELET, BodyPart.LEFT_BRACELET, BodyPart.ARTIFACT_BOOK)) {
            this.type1 = 0;
            this.type2 = 2;
        }
        else {
            this.type1 = 1;
            this.type2 = 1;
        }
    }
    
    @Override
    public ArmorType getItemType() {
        return this.type;
    }
    
    @Override
    public final int getItemMask() {
        return this.type.mask();
    }
    
    @Override
    public void setCrystalType(final CrystalType type) {
        this.crystalType = type;
    }
    
    @Override
    public void setCrystalCount(final int count) {
        this.crystalCount = count;
    }
    
    public void setEnchantable(final Boolean enchantable) {
        this.enchantable = enchantable;
    }
    
    public void setEquipReuseDelay(final int equipReuseDelay) {
        this.equipReuseDelay = equipReuseDelay;
    }
}
