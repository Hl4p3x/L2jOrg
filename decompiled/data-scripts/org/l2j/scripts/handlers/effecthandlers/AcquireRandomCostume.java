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
import org.l2j.gameserver.api.costume.CostumeGrade;
import java.util.EnumSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class AcquireRandomCostume extends AbstractEffect
{
    private final EnumSet<CostumeGrade> grades;
    
    private AcquireRandomCostume(final StatsSet data) {
        this.grades = (EnumSet<CostumeGrade>)data.getStringAsEnumSet("grades", (Class)CostumeGrade.class);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            final Player player = (Player)effected;
            if (player.destroyItem("Consume", item, 1L, (WorldObject)null, true)) {
                CostumeAPI.imprintRandomCostumeOnPlayer(player, (EnumSet)this.grades);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AcquireRandomCostume(data);
        }
        
        public String effectName() {
            return "acquire-random-costume";
        }
    }
}
