// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.ItemSkillType;

public class ItemSkillHolder extends SkillHolder
{
    private final ItemSkillType _type;
    private final int _chance;
    private final int _value;
    
    public ItemSkillHolder(final int skillId, final int skillLvl, final ItemSkillType type, final int chance, final int value) {
        super(skillId, skillLvl);
        this._type = type;
        this._chance = chance;
        this._value = value;
    }
    
    public ItemSkillType getType() {
        return this._type;
    }
    
    public int getChance() {
        return this._chance;
    }
    
    public int getValue() {
        return this._value;
    }
}
