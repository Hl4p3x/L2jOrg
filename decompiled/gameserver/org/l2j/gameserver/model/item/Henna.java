// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import java.util.Collection;
import org.l2j.gameserver.model.stats.Stat;
import java.util.ArrayList;
import java.util.HashMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.List;
import org.l2j.gameserver.model.stats.BaseStats;
import java.util.Map;

public class Henna
{
    private final int _dyeId;
    private final int _dyeItemId;
    private final Map<BaseStats, Integer> _baseStats;
    private final int _wear_fee;
    private final int _wear_count;
    private final int _cancel_fee;
    private final int _cancel_count;
    private final int _duration;
    private final List<Skill> _skills;
    private final List<ClassId> _wear_class;
    
    public Henna(final StatsSet set) {
        this._baseStats = new HashMap<BaseStats, Integer>();
        this._dyeId = set.getInt("dyeId");
        this._dyeItemId = set.getInt("dyeItemId");
        this._baseStats.put(BaseStats.STR, set.getInt("str", 0));
        this._baseStats.put(BaseStats.CON, set.getInt("con", 0));
        this._baseStats.put(BaseStats.DEX, set.getInt("dex", 0));
        this._baseStats.put(BaseStats.INT, set.getInt("int", 0));
        this._baseStats.put(BaseStats.MEN, set.getInt("men", 0));
        this._baseStats.put(BaseStats.WIT, set.getInt("wit", 0));
        this._wear_fee = set.getInt("wear_fee");
        this._wear_count = set.getInt("wear_count");
        this._cancel_fee = set.getInt("cancel_fee");
        this._cancel_count = set.getInt("cancel_count");
        this._duration = set.getInt("duration", -1);
        this._skills = new ArrayList<Skill>();
        this._wear_class = new ArrayList<ClassId>();
    }
    
    public int getDyeId() {
        return this._dyeId;
    }
    
    public int getDyeItemId() {
        return this._dyeItemId;
    }
    
    public int getBaseStats(final Stat stat) {
        return this._baseStats.getOrDefault(stat, 0);
    }
    
    public Map<BaseStats, Integer> getBaseStats() {
        return this._baseStats;
    }
    
    public int getWearFee() {
        return this._wear_fee;
    }
    
    public int getWearCount() {
        return this._wear_count;
    }
    
    public int getCancelFee() {
        return this._cancel_fee;
    }
    
    public int getCancelCount() {
        return this._cancel_count;
    }
    
    public int getDuration() {
        return this._duration;
    }
    
    public List<Skill> getSkills() {
        return this._skills;
    }
    
    public void setSkills(final List<Skill> skillList) {
        this._skills.addAll(skillList);
    }
    
    public List<ClassId> getAllowedWearClass() {
        return this._wear_class;
    }
    
    public boolean isAllowedClass(final ClassId c) {
        return this._wear_class.contains(c);
    }
    
    public void setWearClassIds(final List<ClassId> wearClassIds) {
        this._wear_class.addAll(wearClassIds);
    }
}
