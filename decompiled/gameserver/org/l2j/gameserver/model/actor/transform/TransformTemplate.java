// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.transform;

import java.util.Collections;
import java.util.ArrayList;
import io.github.joealisson.primitive.HashIntMap;
import java.util.Objects;
import org.l2j.gameserver.model.stats.Stat;
import io.github.joealisson.primitive.LinkedHashIntMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumMap;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.List;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.item.type.WeaponType;

public final class TransformTemplate
{
    private final Double collisionRadius;
    private final Double collisionHeight;
    private final WeaponType baseAttackType;
    private final IntMap<TransformLevelData> data;
    private List<SkillHolder> skills;
    private List<AdditionalSkillHolder> additionalSkills;
    private List<AdditionalItemHolder> additionalItems;
    private EnumMap<InventorySlot, Integer> baseDefense;
    private IntMap<Double> baseStats;
    private ExBasicActionList list;
    
    public TransformTemplate(final StatsSet set) {
        this.data = (IntMap<TransformLevelData>)new LinkedHashIntMap(100);
        this.collisionRadius = (set.contains("radius") ? Double.valueOf(set.getDouble("radius")) : null);
        this.collisionHeight = (set.contains("height") ? Double.valueOf(set.getDouble("height")) : null);
        this.baseAttackType = set.getEnum("attackType", WeaponType.class, null);
        if (set.contains("range")) {
            this.addStats(Stat.PHYSICAL_ATTACK_RANGE, set.getDouble("range", 0.0));
        }
        if (set.contains("randomDamage")) {
            this.addStats(Stat.RANDOM_DAMAGE, set.getDouble("randomDamage", 0.0));
        }
        if (set.contains("walk")) {
            this.addStats(Stat.WALK_SPEED, set.getDouble("walk", 0.0));
        }
        if (set.contains("run")) {
            this.addStats(Stat.RUN_SPEED, set.getDouble("run", 0.0));
        }
        if (set.contains("waterWalk")) {
            this.addStats(Stat.SWIM_WALK_SPEED, set.getDouble("waterWalk", 0.0));
        }
        if (set.contains("waterRun")) {
            this.addStats(Stat.SWIM_RUN_SPEED, set.getDouble("waterRun", 0.0));
        }
        if (set.contains("flyWalk")) {
            this.addStats(Stat.FLY_WALK_SPEED, set.getDouble("flyWalk", 0.0));
        }
        if (set.contains("flyRun")) {
            this.addStats(Stat.FLY_RUN_SPEED, set.getDouble("flyRun", 0.0));
        }
        if (set.contains("pAtk")) {
            this.addStats(Stat.PHYSICAL_ATTACK, set.getDouble("pAtk", 0.0));
        }
        if (set.contains("mAtk")) {
            this.addStats(Stat.MAGIC_ATTACK, set.getDouble("mAtk", 0.0));
        }
        if (set.contains("range")) {
            this.addStats(Stat.PHYSICAL_ATTACK_RANGE, set.getInt("range", 0));
        }
        if (set.contains("attackSpeed")) {
            this.addStats(Stat.PHYSICAL_ATTACK_SPEED, set.getInt("attackSpeed", 0));
        }
        if (set.contains("critRate")) {
            this.addStats(Stat.CRITICAL_RATE, set.getInt("critRate", 0));
        }
        if (set.contains("str")) {
            this.addStats(Stat.STAT_STR, set.getInt("str", 0));
        }
        if (set.contains("int")) {
            this.addStats(Stat.STAT_INT, set.getInt("int", 0));
        }
        if (set.contains("con")) {
            this.addStats(Stat.STAT_CON, set.getInt("con", 0));
        }
        if (set.contains("dex")) {
            this.addStats(Stat.STAT_DEX, set.getInt("dex", 0));
        }
        if (set.contains("wit")) {
            this.addStats(Stat.STAT_WIT, set.getInt("wit", 0));
        }
        if (set.contains("men")) {
            this.addStats(Stat.STAT_MEN, set.getInt("men", 0));
        }
        if (set.contains("chest")) {
            this.addDefense(InventorySlot.CHEST, set.getInt("chest", 0));
        }
        if (set.contains("legs")) {
            this.addDefense(InventorySlot.LEGS, set.getInt("legs", 0));
        }
        if (set.contains("head")) {
            this.addDefense(InventorySlot.HEAD, set.getInt("head", 0));
        }
        if (set.contains("feet")) {
            this.addDefense(InventorySlot.FEET, set.getInt("feet", 0));
        }
        if (set.contains("gloves")) {
            this.addDefense(InventorySlot.GLOVES, set.getInt("gloves", 0));
        }
        if (set.contains("underwear")) {
            this.addDefense(InventorySlot.PENDANT, set.getInt("underwear", 0));
        }
        if (set.contains("cloak")) {
            this.addDefense(InventorySlot.CLOAK, set.getInt("cloak", 0));
        }
        if (set.contains("rear")) {
            this.addDefense(InventorySlot.RIGHT_EAR, set.getInt("rear", 0));
        }
        if (set.contains("lear")) {
            this.addDefense(InventorySlot.LEFT_EAR, set.getInt("lear", 0));
        }
        if (set.contains("rfinger")) {
            this.addDefense(InventorySlot.RIGHT_FINGER, set.getInt("rfinger", 0));
        }
        if (set.contains("lfinger")) {
            this.addDefense(InventorySlot.LEFT_FINGER, set.getInt("lfinger", 0));
        }
        if (set.contains("neck")) {
            this.addDefense(InventorySlot.NECK, set.getInt("neck", 0));
        }
    }
    
    private void addDefense(final InventorySlot slot, final int val) {
        if (Objects.isNull(this.baseDefense)) {
            this.baseDefense = new EnumMap<InventorySlot, Integer>(InventorySlot.class);
        }
        this.baseDefense.put(slot, val);
    }
    
    public int getDefense(final InventorySlot slot, final int defaultValue) {
        return Objects.isNull(this.baseDefense) ? defaultValue : this.baseDefense.getOrDefault(slot, defaultValue);
    }
    
    private void addStats(final Stat stat, final double val) {
        if (this.baseStats == null) {
            this.baseStats = (IntMap<Double>)new HashIntMap();
        }
        this.baseStats.put(stat.ordinal(), (Object)val);
    }
    
    public double getStats(final Stat stat, final double defaultValue) {
        return (double)((this.baseStats == null) ? defaultValue : this.baseStats.getOrDefault(stat.ordinal(), (Object)defaultValue));
    }
    
    public Double getCollisionRadius() {
        return this.collisionRadius;
    }
    
    public Double getCollisionHeight() {
        return this.collisionHeight;
    }
    
    public WeaponType getBaseAttackType() {
        return this.baseAttackType;
    }
    
    public void addSkill(final SkillHolder holder) {
        if (this.skills == null) {
            this.skills = new ArrayList<SkillHolder>();
        }
        this.skills.add(holder);
    }
    
    public List<SkillHolder> getSkills() {
        return (this.skills != null) ? this.skills : Collections.emptyList();
    }
    
    public void addAdditionalSkill(final AdditionalSkillHolder holder) {
        if (this.additionalSkills == null) {
            this.additionalSkills = new ArrayList<AdditionalSkillHolder>();
        }
        this.additionalSkills.add(holder);
    }
    
    public List<AdditionalSkillHolder> getAdditionalSkills() {
        return (this.additionalSkills != null) ? this.additionalSkills : Collections.emptyList();
    }
    
    public void addAdditionalItem(final AdditionalItemHolder holder) {
        if (this.additionalItems == null) {
            this.additionalItems = new ArrayList<AdditionalItemHolder>();
        }
        this.additionalItems.add(holder);
    }
    
    public List<AdditionalItemHolder> getAdditionalItems() {
        return (this.additionalItems != null) ? this.additionalItems : Collections.emptyList();
    }
    
    public ExBasicActionList getBasicActionList() {
        return this.list;
    }
    
    public void setBasicActionList(final ExBasicActionList list) {
        this.list = list;
    }
    
    public boolean hasBasicActionList() {
        return this.list != null;
    }
    
    public void addLevelData(final TransformLevelData data) {
        this.data.put(data.getLevel(), (Object)data);
    }
    
    public TransformLevelData getData(final int level) {
        return (TransformLevelData)this.data.get(level);
    }
}
