// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.elemental;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import io.github.joealisson.primitive.IntMap;

public class ElementalSpiritTemplate
{
    private final byte type;
    private final byte stage;
    private final int npcId;
    private final int maxCharacteristics;
    private final int extractItem;
    private IntMap<SpiritLevel> levels;
    private List<ItemHolder> itemsToEvolve;
    private List<AbsorbItem> absorbItems;
    
    ElementalSpiritTemplate(final byte type, final byte stage, final int npcId, final int extractItem, final int maxCharacteristics) {
        this.type = type;
        this.stage = stage;
        this.npcId = npcId;
        this.extractItem = extractItem;
        this.maxCharacteristics = maxCharacteristics;
        this.levels = (IntMap<SpiritLevel>)new HashIntMap(10);
    }
    
    void addLevelInfo(final int level, final int attack, final int defense, final int criticalRate, final int criticalDamage, final long maxExperience) {
        final SpiritLevel spiritLevel = new SpiritLevel();
        spiritLevel.attack = attack;
        spiritLevel.defense = defense;
        spiritLevel.criticalRate = criticalRate;
        spiritLevel.criticalDamage = criticalDamage;
        spiritLevel.maxExperience = maxExperience;
        this.levels.put(level, (Object)spiritLevel);
    }
    
    void addItemToEvolve(final Integer itemId, final Integer count) {
        if (Objects.isNull(this.itemsToEvolve)) {
            this.itemsToEvolve = new ArrayList<ItemHolder>(2);
        }
        this.itemsToEvolve.add(new ItemHolder(itemId, count));
    }
    
    public byte getType() {
        return this.type;
    }
    
    public byte getStage() {
        return this.stage;
    }
    
    public int getNpcId() {
        return this.npcId;
    }
    
    public long getMaxExperienceAtLevel(final byte level) {
        return ((SpiritLevel)this.levels.get((int)level)).maxExperience;
    }
    
    public int getMaxLevel() {
        return this.levels.size();
    }
    
    public int getAttackAtLevel(final byte level) {
        return ((SpiritLevel)this.levels.get((int)level)).attack;
    }
    
    public int getDefenseAtLevel(final byte level) {
        return ((SpiritLevel)this.levels.get((int)level)).defense;
    }
    
    public int getCriticalRateAtLevel(final byte level) {
        return ((SpiritLevel)this.levels.get((int)level)).criticalRate;
    }
    
    public int getCriticalDamageAtLevel(final byte level) {
        return ((SpiritLevel)this.levels.get((int)level)).criticalDamage;
    }
    
    public int getMaxCharacteristics() {
        return this.maxCharacteristics;
    }
    
    public List<ItemHolder> getItemsToEvolve() {
        return Objects.isNull(this.itemsToEvolve) ? Collections.emptyList() : this.itemsToEvolve;
    }
    
    void addAbsorbItem(final Integer itemId, final Integer experience) {
        if (Objects.isNull(this.absorbItems)) {
            this.absorbItems = new ArrayList<AbsorbItem>();
        }
        this.absorbItems.add(new AbsorbItem(itemId, experience));
    }
    
    public List<AbsorbItem> getAbsorbItems() {
        return Objects.isNull(this.absorbItems) ? Collections.emptyList() : this.absorbItems;
    }
    
    public int getExtractItem() {
        return this.extractItem;
    }
    
    private static class SpiritLevel
    {
        long maxExperience;
        int criticalDamage;
        int criticalRate;
        int defense;
        int attack;
    }
}
