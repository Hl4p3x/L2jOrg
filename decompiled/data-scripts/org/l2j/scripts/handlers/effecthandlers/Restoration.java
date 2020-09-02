// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Restoration extends AbstractEffect
{
    private final int itemId;
    private final int itemCount;
    private final int itemEnchantmentLevel;
    
    private Restoration(final StatsSet params) {
        this.itemId = params.getInt("item", 0);
        this.itemCount = params.getInt("count", 0);
        this.itemEnchantmentLevel = params.getInt("enchant", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayable((WorldObject)effected)) {
            return;
        }
        if (this.itemId <= 0 || this.itemCount <= 0) {
            effected.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
            Restoration.LOGGER.warn("effect with wrong item Id/count: {}/{}!", (Object)this.itemId, (Object)this.itemCount);
            return;
        }
        if (GameUtils.isPlayer((WorldObject)effected)) {
            final Item newItem = effected.getActingPlayer().addItem("Skill", this.itemId, (long)this.itemCount, (WorldObject)effector, true);
            if (Objects.nonNull(newItem) && this.itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(this.itemEnchantmentLevel);
            }
        }
        else if (GameUtils.isPet((WorldObject)effected)) {
            final Item newItem = effected.getInventory().addItem("Skill", this.itemId, (long)this.itemCount, effected.getActingPlayer(), (Object)effector);
            if (this.itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(this.itemEnchantmentLevel);
            }
            effected.getActingPlayer().sendPacket(new ServerPacket[] { (ServerPacket)new PetItemList(effected.getInventory().getItems()) });
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.EXTRACT_ITEM;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Restoration(data);
        }
        
        public String effectName() {
            return "restoration";
        }
    }
}
