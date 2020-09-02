// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.StorageType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class EnlargeSlot extends AbstractEffect
{
    private final StorageType type;
    private final double power;
    
    private EnlargeSlot(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.type = (StorageType)params.getEnum("type", (Class)StorageType.class, (Enum)StorageType.INVENTORY_NORMAL);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        Stat stat2 = null;
        switch (this.type) {
            case TRADE_BUY: {
                stat2 = Stat.TRADE_BUY;
                break;
            }
            case TRADE_SELL: {
                stat2 = Stat.TRADE_SELL;
                break;
            }
            case RECIPE_DWARVEN: {
                stat2 = Stat.RECIPE_DWARVEN;
                break;
            }
            case RECIPE_COMMON: {
                stat2 = Stat.RECIPE_COMMON;
                break;
            }
            case STORAGE_PRIVATE: {
                stat2 = Stat.STORAGE_PRIVATE;
                break;
            }
            default: {
                stat2 = Stat.INVENTORY_NORMAL;
                break;
            }
        }
        final Stat stat = stat2;
        effected.getStats().mergeAdd(stat, this.power);
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.sendPacket(new ServerPacket[] { (ServerPacket)new ExStorageMaxCount((Player)effected) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new EnlargeSlot(data);
        }
        
        public String effectName() {
            return "enlarge-slot";
        }
    }
}
