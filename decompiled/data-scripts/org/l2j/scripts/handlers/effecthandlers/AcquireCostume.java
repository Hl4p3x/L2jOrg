// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.api.costume.CostumeAPI;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class AcquireCostume extends AbstractEffect
{
    private final int id;
    
    private AcquireCostume(final StatsSet data) {
        this.id = data.getInt("id");
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            final Player player = (Player)effected;
            if (player.destroyItem("Consume", item, 1L, (WorldObject)null, true)) {
                CostumeAPI.imprintCostumeOnPlayer(player, this.id);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AcquireCostume(data);
        }
        
        public String effectName() {
            return "acquire-costume";
        }
    }
}
