// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.Collections;
import java.util.HashSet;
import java.util.EnumMap;
import org.l2j.gameserver.model.StatsSet;
import java.util.Set;
import org.l2j.gameserver.enums.SkillEnchantType;
import java.util.Map;

public class EnchantSkillHolder
{
    private final int _level;
    private final int _enchantFailLevel;
    private final Map<SkillEnchantType, Long> _sp;
    private final Map<SkillEnchantType, Integer> _chance;
    private final Map<SkillEnchantType, Set<ItemHolder>> _requiredItems;
    
    public EnchantSkillHolder(final StatsSet set) {
        this._sp = new EnumMap<SkillEnchantType, Long>(SkillEnchantType.class);
        this._chance = new EnumMap<SkillEnchantType, Integer>(SkillEnchantType.class);
        this._requiredItems = new EnumMap<SkillEnchantType, Set<ItemHolder>>(SkillEnchantType.class);
        this._level = set.getInt("level");
        this._enchantFailLevel = set.getInt("enchantFailLevel");
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getEnchantFailLevel() {
        return this._enchantFailLevel;
    }
    
    public void addSp(final SkillEnchantType type, final long sp) {
        this._sp.put(type, sp);
    }
    
    public long getSp(final SkillEnchantType type) {
        return this._sp.getOrDefault(type, 0L);
    }
    
    public void addChance(final SkillEnchantType type, final int chance) {
        this._chance.put(type, chance);
    }
    
    public int getChance(final SkillEnchantType type) {
        return this._chance.getOrDefault(type, 100);
    }
    
    public void addRequiredItem(final SkillEnchantType type, final ItemHolder item) {
        this._requiredItems.computeIfAbsent(type, k -> new HashSet()).add(item);
    }
    
    public Set<ItemHolder> getRequiredItems(final SkillEnchantType type) {
        return this._requiredItems.getOrDefault(type, Collections.emptySet());
    }
}
