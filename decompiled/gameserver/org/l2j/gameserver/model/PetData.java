// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.Iterator;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetData
{
    private final Map<Integer, PetLevelData> _levelStats;
    private final List<PetSkillLearn> _skills;
    private final int _npcId;
    private final int _itemId;
    private final List<Integer> _food;
    private int _load;
    private int _hungryLimit;
    private int _minlvl;
    private int _maxlvl;
    private boolean _syncLevel;
    
    public PetData(final int npcId, final int itemId) {
        this._levelStats = new HashMap<Integer, PetLevelData>();
        this._skills = new ArrayList<PetSkillLearn>();
        this._food = new ArrayList<Integer>();
        this._load = 20000;
        this._hungryLimit = 1;
        this._minlvl = 127;
        this._maxlvl = 0;
        this._syncLevel = false;
        this._npcId = npcId;
        this._itemId = itemId;
    }
    
    public int getNpcId() {
        return this._npcId;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public void addNewStat(final int level, final PetLevelData data) {
        if (this._minlvl > level) {
            this._minlvl = level;
        }
        if (this._maxlvl < level - 1) {
            this._maxlvl = level - 1;
        }
        this._levelStats.put(level, data);
    }
    
    public PetLevelData getPetLevelData(final int petLevel) {
        return this._levelStats.get(petLevel);
    }
    
    public int getLoad() {
        return this._load;
    }
    
    public void setLoad(final int load) {
        this._load = load;
    }
    
    public int getHungryLimit() {
        return this._hungryLimit;
    }
    
    public void setHungryLimit(final int limit) {
        this._hungryLimit = limit;
    }
    
    public boolean isSynchLevel() {
        return this._syncLevel;
    }
    
    public int getMinLevel() {
        return this._minlvl;
    }
    
    public int getMaxLevel() {
        return this._maxlvl;
    }
    
    public List<Integer> getFood() {
        return this._food;
    }
    
    public void addFood(final Integer foodId) {
        this._food.add(foodId);
    }
    
    public void setSyncLevel(final boolean val) {
        this._syncLevel = val;
    }
    
    public void addNewSkill(final int skillId, final int skillLvl, final int petLvl) {
        this._skills.add(new PetSkillLearn(skillId, skillLvl, petLvl));
    }
    
    public int getAvailableLevel(final int skillId, final int petLvl) {
        int lvl = 0;
        boolean found = false;
        for (final PetSkillLearn temp : this._skills) {
            if (temp.getSkillId() != skillId) {
                continue;
            }
            found = true;
            if (temp.getLevel() == 0) {
                if (petLvl < 70) {
                    lvl = petLvl / 10;
                    if (lvl <= 0) {
                        lvl = 1;
                    }
                }
                else {
                    lvl = 7 + (petLvl - 70) / 5;
                }
                final int maxLvl = SkillEngine.getInstance().getMaxLevel(temp.getSkillId());
                if (lvl > maxLvl) {
                    lvl = maxLvl;
                    break;
                }
                break;
            }
            else {
                if (temp.getMinLevel() > petLvl || temp.getLevel() <= lvl) {
                    continue;
                }
                lvl = temp.getLevel();
            }
        }
        if (found && lvl == 0) {
            return 1;
        }
        return lvl;
    }
    
    public List<PetSkillLearn> getAvailableSkills() {
        return this._skills;
    }
    
    public static final class PetSkillLearn extends SkillHolder
    {
        private final int _minLevel;
        
        public PetSkillLearn(final int id, final int lvl, final int minLvl) {
            super(id, lvl);
            this._minLevel = minLvl;
        }
        
        public int getMinLevel() {
            return this._minLevel;
        }
    }
}
