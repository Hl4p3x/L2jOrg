// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.HashSet;
import java.util.ArrayList;
import org.l2j.gameserver.model.base.SocialClass;
import java.util.Set;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public final class SkillLearn
{
    private final String _skillName;
    private final int _skillId;
    private final int _skillLvl;
    private final int _getLevel;
    private final int _getDualClassLevel;
    private final boolean _autoGet;
    private final long _levelUpSp;
    private final List<ItemHolder> _requiredItems;
    private final List<Race> _races;
    private final List<SkillHolder> _preReqSkills;
    private final boolean _residenceSkill;
    private final List<Integer> _residenceIds;
    private final boolean _learnedByNpc;
    private final boolean _learnedByFS;
    private final Set<Integer> _removeSkills;
    private final int _treeId;
    private final int _row;
    private final int _column;
    private final int _pointsRequired;
    private SocialClass _socialClass;
    
    public SkillLearn(final StatsSet set) {
        this._requiredItems = new ArrayList<ItemHolder>();
        this._races = new ArrayList<Race>();
        this._preReqSkills = new ArrayList<SkillHolder>();
        this._residenceIds = new ArrayList<Integer>();
        this._removeSkills = new HashSet<Integer>(1);
        this._skillName = set.getString("skillName");
        this._skillId = set.getInt("skillId");
        this._skillLvl = set.getInt("skillLvl");
        this._getLevel = set.getInt("getLevel");
        this._getDualClassLevel = set.getInt("getDualClassLevel", 0);
        this._autoGet = set.getBoolean("autoGet", false);
        this._levelUpSp = set.getLong("levelUpSp", 0L);
        this._residenceSkill = set.getBoolean("residenceSkill", false);
        this._learnedByNpc = set.getBoolean("learnedByNpc", false);
        this._learnedByFS = set.getBoolean("learnedByFS", false);
        this._treeId = set.getInt("treeId", 0);
        this._row = set.getInt("row", 0);
        this._column = set.getInt("row", 0);
        this._pointsRequired = set.getInt("pointsRequired", 0);
    }
    
    public String getName() {
        return this._skillName;
    }
    
    public int getSkillId() {
        return this._skillId;
    }
    
    public int getSkillLevel() {
        return this._skillLvl;
    }
    
    public int getGetLevel() {
        return this._getLevel;
    }
    
    public int getDualClassLevel() {
        return this._getDualClassLevel;
    }
    
    public long getLevelUpSp() {
        return this._levelUpSp;
    }
    
    public boolean isAutoGet() {
        return this._autoGet;
    }
    
    public List<ItemHolder> getRequiredItems() {
        return this._requiredItems;
    }
    
    public void addRequiredItem(final ItemHolder item) {
        this._requiredItems.add(item);
    }
    
    public List<Race> getRaces() {
        return this._races;
    }
    
    public void addRace(final Race race) {
        this._races.add(race);
    }
    
    public List<SkillHolder> getPreReqSkills() {
        return this._preReqSkills;
    }
    
    public void addPreReqSkill(final SkillHolder skill) {
        this._preReqSkills.add(skill);
    }
    
    public SocialClass getSocialClass() {
        return this._socialClass;
    }
    
    public void setSocialClass(final SocialClass socialClass) {
        if (this._socialClass == null) {
            this._socialClass = socialClass;
        }
    }
    
    public boolean isResidencialSkill() {
        return this._residenceSkill;
    }
    
    public List<Integer> getResidenceIds() {
        return this._residenceIds;
    }
    
    public void addResidenceId(final Integer id) {
        this._residenceIds.add(id);
    }
    
    public boolean isLearnedByNpc() {
        return this._learnedByNpc;
    }
    
    public boolean isLearnedByFS() {
        return this._learnedByFS;
    }
    
    public void addRemoveSkills(final int skillId) {
        this._removeSkills.add(skillId);
    }
    
    public Set<Integer> getRemoveSkills() {
        return this._removeSkills;
    }
    
    public int getTreeId() {
        return this._treeId;
    }
    
    public int getRow() {
        return this._row;
    }
    
    public int getColumn() {
        return this._column;
    }
    
    public int getPointsRequired() {
        return this._pointsRequired;
    }
    
    public Skill getSkill() {
        return SkillEngine.getInstance().getSkill(this._skillId, this._skillLvl);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/engine/skill/api/Skill;IIII)Ljava/lang/String;, this.getSkill(), this._treeId, this._row, this._column, this._pointsRequired);
    }
}
