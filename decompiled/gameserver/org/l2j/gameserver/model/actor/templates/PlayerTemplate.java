// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.templates;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumMap;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.l2j.gameserver.model.base.ClassId;

public class PlayerTemplate extends CreatureTemplate
{
    private final ClassId classId;
    private final float[] baseHp;
    private final float[] baseMp;
    private final float[] baseCp;
    private final double[] baseHpReg;
    private final double[] baseMpReg;
    private final double[] baseCpReg;
    private final double fCollisionHeightFemale;
    private final double fCollisionRadiusFemale;
    private final int baseSafeFallHeight;
    private final List<Location> creationPoints;
    private final EnumMap<InventorySlot, Integer> baseSlotDef;
    
    public PlayerTemplate(final StatsSet set, final List<Location> creationPoints) {
        super(set);
        this.classId = ClassId.getClassId(set.getInt("classId"));
        this.setRace(this.classId.getRace());
        final int maxLevel = LevelData.getInstance().getMaxLevel() + 1;
        this.baseHp = new float[maxLevel];
        this.baseMp = new float[maxLevel];
        this.baseCp = new float[maxLevel];
        this.baseHpReg = new double[maxLevel];
        this.baseMpReg = new double[maxLevel];
        this.baseCpReg = new double[maxLevel];
        (this.baseSlotDef = new EnumMap<InventorySlot, Integer>(InventorySlot.class)).put(InventorySlot.CHEST, set.getInt("basePDefchest", 0));
        this.baseSlotDef.put(InventorySlot.LEGS, set.getInt("basePDeflegs", 0));
        this.baseSlotDef.put(InventorySlot.HEAD, set.getInt("basePDefhead", 0));
        this.baseSlotDef.put(InventorySlot.FEET, set.getInt("basePDeffeet", 0));
        this.baseSlotDef.put(InventorySlot.GLOVES, set.getInt("basePDefgloves", 0));
        this.baseSlotDef.put(InventorySlot.PENDANT, set.getInt("basePDefunderwear", 0));
        this.baseSlotDef.put(InventorySlot.CLOAK, set.getInt("basePDefcloak", 0));
        this.baseSlotDef.put(InventorySlot.RIGHT_EAR, set.getInt("baseMDefrear", 0));
        this.baseSlotDef.put(InventorySlot.LEFT_EAR, set.getInt("baseMDeflear", 0));
        this.baseSlotDef.put(InventorySlot.RIGHT_FINGER, set.getInt("baseMDefrfinger", 0));
        this.baseSlotDef.put(InventorySlot.LEFT_FINGER, set.getInt("baseMDefrfinger", 0));
        this.baseSlotDef.put(InventorySlot.NECK, set.getInt("baseMDefneck", 0));
        this.fCollisionRadiusFemale = set.getDouble("collisionFemaleradius");
        this.fCollisionHeightFemale = set.getDouble("collisionFemaleheight");
        this.baseSafeFallHeight = set.getInt("baseSafeFall", 333);
        this.creationPoints = creationPoints;
    }
    
    public ClassId getClassId() {
        return this.classId;
    }
    
    public Location getCreationPoint() {
        return this.creationPoints.get(Rnd.get(this.creationPoints.size()));
    }
    
    public void setUpgainValue(final String paramName, final int level, final double val) {
        switch (paramName) {
            case "hp": {
                this.baseHp[level] = (float)val;
                break;
            }
            case "mp": {
                this.baseMp[level] = (float)val;
                break;
            }
            case "cp": {
                this.baseCp[level] = (float)val;
                break;
            }
            case "hpRegen": {
                this.baseHpReg[level] = val;
                break;
            }
            case "mpRegen": {
                this.baseMpReg[level] = val;
                break;
            }
            case "cpRegen": {
                this.baseCpReg[level] = val;
                break;
            }
        }
    }
    
    public float getBaseHpMax(final int level) {
        return this.baseHp[level];
    }
    
    public float getBaseMpMax(final int level) {
        return this.baseMp[level];
    }
    
    public float getBaseCpMax(final int level) {
        return this.baseCp[level];
    }
    
    public double getBaseHpRegen(final int level) {
        return this.baseHpReg[level];
    }
    
    public double getBaseMpRegen(final int level) {
        return this.baseMpReg[level];
    }
    
    public double getBaseCpRegen(final int level) {
        return this.baseCpReg[level];
    }
    
    public int getBaseDefBySlot(final InventorySlot slot) {
        return this.baseSlotDef.getOrDefault(slot, 0);
    }
    
    public double getFCollisionHeightFemale() {
        return this.fCollisionHeightFemale;
    }
    
    public double getFCollisionRadiusFemale() {
        return this.fCollisionRadiusFemale;
    }
    
    public int getSafeFallHeight() {
        return this.baseSafeFallHeight;
    }
}
